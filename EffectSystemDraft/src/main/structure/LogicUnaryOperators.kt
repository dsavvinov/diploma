package main.structure

class Not(override val arg: Node) : UnaryOperator {
    override fun accept(visitor: Visitor): Node {
        return visitor.visit(this)
    }

//    override fun isImplies(stmt: LogicStatement): Boolean {
//        return !arg.isImplies(stmt)
//    }
}