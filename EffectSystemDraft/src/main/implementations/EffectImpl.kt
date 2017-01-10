package main.implementations

import main.structure.Effect
import main.structure.Node
import main.structure.Operator
import main.structure.SchemaVisitor

data class EffectImpl(override val premise: Node, override val conclusion: Node) : Effect {
    override fun accept(visitor: SchemaVisitor): Node {
        return visitor.visit(this)
    }

    override fun isImplies(op: Operator): Boolean {
        return false
    }
}