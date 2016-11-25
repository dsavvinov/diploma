package system

import tests.*

/**
 * Created by dsavvinov on 25.11.16.
 */


class EffectSchema : Expression() {
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

    override fun bind(context: Map<Variable, Expression>): EffectSchema {
        for ((bindee, binder) in context) {
            bindVar(bindee, binder)
        }

        return this
    }

    override fun evaluate(): EffectSchema {
        // bind vars
        assertions.forEach { it.bind(bindVars) }

        val effects: EffectSchema = EffectSchema()

        val freeVars = getFreeVars()
        if (freeVars.isNotEmpty()) {
            throw IllegalArgumentException("Variables $freeVars are not bound; can't evaluate!")
        }

        // Add all assertions
        for (assertion in assertions) {
            assertion.evaluate().forEach { effects.addAssertion(it) }
        }

        // Remove false assertions
        effects.assertions.removeAll { it.premise is Premise.Const && it.premise.const == FALSE }
        return effects
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
}
