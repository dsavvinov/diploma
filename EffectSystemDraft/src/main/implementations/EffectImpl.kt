package main.implementations

import main.structure.Context
import main.structure.Effect
import main.structure.Node

data class EffectImpl(override val premise: Node, override val conclusion: Node) : Effect {
    override fun visit(context: Context): Effect {
        val evaluatedPremise = premise.evaluate(context)
        val evaluatedConclusion = conclusion.evaluate(context)

        return EffectImpl(premise, conclusion)
    }
}