package main.structure

data class Or(override val left: Node, override val right: Node) : BinaryOperator {
    override fun accept(visitor: Visitor): Node {
        return visitor.visit(this)
    }

    override fun isImplies(op: Operator): Boolean {
        return left.isImplies(op) || right.isImplies(op)
    }
}

data class And(override val left: Node, override val right: Node) : BinaryOperator {
    override fun accept(visitor: Visitor): Node {
        return visitor.visit(this)
    }

    override fun isImplies(op: Operator): Boolean {
        // TODO: think about additions to context
        return left.isImplies(op) && right.isImplies(op)
    }
}

