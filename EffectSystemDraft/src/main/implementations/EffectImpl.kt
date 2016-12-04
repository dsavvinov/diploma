package main.implementations

import main.structure.Effect
import main.structure.LogicStatement
import main.structure.Node
import main.structure.Visitor

data class EffectImpl(override val premise: LogicStatement, override val conclusion: LogicStatement) : Effect {
    override fun accept(visitor: Visitor): Effect {
        return visitor.visit(this)
    }
}