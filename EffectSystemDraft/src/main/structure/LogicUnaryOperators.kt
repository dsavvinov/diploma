package main.structure

class Not(override val arg: LogicStatement) : LogicUnaryOperator {
    override fun accept(visitor: Visitor): Node {
        return visitor.visit(this)
    }
}