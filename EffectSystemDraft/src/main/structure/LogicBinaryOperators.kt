package main.structure

data class Or(override val left: Node, override val right: Node) : BinaryOperator {
    override fun accept(visitor: Visitor): Node {
        return visitor.visit(this)
    }

//    override fun isImplies(stmt: LogicStatement): Boolean {
//        return left.isImplies(stmt) || right.isImplies(stmt)
//    }
}

data class And(override val left: Node, override val right: Node) : BinaryOperator {
    override fun accept(visitor: Visitor): Node {
        return visitor.visit(this)
    }

//    override fun isImplies(stmt: LogicStatement): Boolean {
//        // TODO: think about additions to context
//        return left.isImplies(stmt) && right.isImplies(stmt)
//    }
}

