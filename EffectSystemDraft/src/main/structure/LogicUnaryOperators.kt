package main.structure

class Not(override val arg: LogicStatement) : LogicUnaryOperator {
    override fun accept(visitor: Visitor): LogicStatement {
        return visitor.visit(this)
    }

    override fun isImplies(stmt: LogicStatement): Boolean {
        return !arg.isImplies(stmt)
    }
}