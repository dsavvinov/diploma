package main.implementations

import main.structure.Effect
import main.structure.Node
import main.structure.Visitor

data class EffectImpl(override val premise: Node, override val conclusion: Node) : Effect {
    override fun accept(visitor: Visitor): Effect {
        return visitor.visit(this)
    }
}