package main.implementations.operators

import main.structure.Node
import main.structure.Operator
import main.structure.UnaryOperator
import main.structure.Visitor

class Not(override val arg: Node) : UnaryOperator {
    override fun accept(visitor: Visitor): Node {
        return visitor.visit(this)
    }

    override fun isImplies(op: Operator): Boolean {
        return !arg.isImplies(op)
    }
}