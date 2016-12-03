package main.structure

class Is(override val left: Node, override val right: Type) : BinaryOperator, LogicStatement {
    override fun accept(visitor: Visitor): Node {
        return visitor.visit(this)
    }
}

class Equal(override val left: Node, override val right: Node) : BinaryOperator, LogicStatement {
    override fun accept(visitor: Visitor): Node {
        return visitor.visit(this)
    }
}