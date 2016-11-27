package system

class EffectSchemaBuilder {
    val assertions: MutableList<Assertion> = mutableListOf()
    val bindVars: MutableMap<Variable, Expression> = mutableMapOf()
    val args: MutableSet<Variable> = mutableSetOf()

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
        return EffectSchema(assertions, bindVars, args)
    }
}

class EffectSchema (
        val assertions: List<Assertion> = listOf(),
        val bindVars: Map<Variable, Expression> = mapOf(),
        val args: Set<Variable> = setOf()
) : Expression() {

    override fun bind(context: Map<Variable, Expression>): EffectSchema {
        val newMap = mutableMapOf<Variable, Expression>()
        newMap.putAll(bindVars)
        newMap.putAll(context)
        val effectSchema = EffectSchema(assertions, newMap, args)
        return effectSchema
    }

    override fun evaluate(): EffectSchema {
        // bind vars
        assertions.forEach { it.bind(bindVars) }

        val effects: EffectSchemaBuilder = EffectSchemaBuilder()

        val freeVars = getFreeVars()
        if (freeVars.isNotEmpty()) {
            throw IllegalArgumentException("Variables $freeVars are not bound; can't evaluate!")
        }

        // Add all non-false assertions
        for (assertion in assertions) {
            assertion.evaluate().forEach {
                if (it.premise !is Premise.Const || it.premise.const != FALSE) {
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

    fun collectAt(effect: Effect): List<Premise> {
        return assertions.filter { it.effect == effect }.map { it.premise }
    }

    fun collectExcept(effect: Effect): List<Premise> {
        return assertions.filter { it.effect != effect }.map { it.premise }
    }
}
