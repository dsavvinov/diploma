package main.structure

data class Or(override val left: LogicStatement, override val right: LogicStatement) : LogicBinaryOperator {
    override fun accept(visitor: Visitor): Node {
        return visitor.visit(this)
    }
}

data class And(override val left: LogicStatement, override val right: LogicStatement) : LogicBinaryOperator {
    override fun accept(visitor: Visitor): Node {
        return visitor.visit(this)
    }
}

