package main.implementations.operators

import main.structure.Node
import main.structure.Operator
import main.structure.UnaryOperator
import main.structure.SchemaVisitor

class Not(override val arg: Node) : UnaryOperator {
    override fun accept(visitor: SchemaVisitor): Node {
        return visitor.visit(this)
    }

    override fun isImplies(op: Operator): Boolean {
        return !arg.isImplies(op)
    }
}