package main.system

class EffectSchemaBuilder {
    val assertions: MutableList<Assertion> = mutableListOf()
    val bindVars: MutableMap<Variable, Expression> = mutableMapOf()
    val args: MutableSet<Variable> = mutableSetOf()
    var returnVar: Variable? = null

    fun addVar(identifier: Variable) {
        if (identifier in args) {
            throw IllegalArgumentException("Variable $identifier already exists!")
        }
        args += identifier
    }

    fun addAssertion(premise: Premise, effect: Effect) {
        if (premise.lhs !in args) {
            throw IllegalArgumentException("Variable ${premise.lhs} not defined, can't add premise $premise")
        }
        assertions += Assertion(premise, effect)
    }

    fun addAssertion(assertion: Assertion) {
        assertions += assertion
    }

    fun bindVar(bindee: Variable, binder: Expression) {
        if (bindee !in args) {
            throw IllegalArgumentException("Variable $bindee not defined, can't bind it!")
        }
        bindVars[bindee] = binder
    }

    fun build() : EffectSchema {
        return EffectSchema(assertions, bindVars, args, returnVar!!)
    }
}

open class EffectSchema (
        val assertions: List<Assertion> = listOf(),
        val bindVars: Map<Variable, Expression> = mapOf(),
        val args: Set<Variable> = setOf(),
        val returnVar: Variable? = null
) : Expression, LValue {

    override fun evaluate(context: Map<Variable, EffectSchema>): EffectSchema {
        val effects: EffectSchemaBuilder = EffectSchemaBuilder()

        val freeVars = getFreeVars()
        if (freeVars.isNotEmpty()) {
            throw IllegalArgumentException("Variables $freeVars are not bound; can't evaluate!")
        }

        // Add all non-false assertions
        for (assertion in assertions) {
            assertion.evaluate(context).forEach {
                if (it.premise !is Premise.Const || (it.premise as Premise.Const).const != FALSE) {
                    effects.addAssertion(it)
                }
            }
        }

        return effects.build()
    }

    fun getFreeVars(): List<Variable> {
        return (args - bindVars.keys).toList()
    }

    override fun toString(): String {
        return StringBuilder().apply {
            assertions.forEach(::println)
        }.toString()
    }

    fun collectAt(predicate: (Effect) -> Boolean): List<Premise> {
        return assertions.filter { predicate(it.effect) }.map { it.premise }
    }

    override fun equalTo(other: LValue) : LValue {
        val res = EffectSchemaBuilder()

        for ((thisPremise, thisEffect) in assertions) {
            if (thisEffect !is Effect.Returns) {
                continue
            }
            for ((otherPremise, otherEffect) in other.assertions) {
                if (otherEffect !is Effect.Returns) {
                    continue
                }
                if (thisPremise.implies(otherPremise)) {
                    res.addAssertion(otherPremise to Effect.Returns(
                            if (thisEffect == otherEffect) TRUE_VAL else FALSE_VAL)
                    )
                }
            }
        }

        return res.build()
    }
}
