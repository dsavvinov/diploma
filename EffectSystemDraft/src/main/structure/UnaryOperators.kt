package main.structure

class Throws(override val arg: Exception) : UnaryOperator {
    override fun accept(visitor: Visitor): Throws {
        return visitor.visit(this)
    }

//    override fun isImplies(stmt: LogicStatement): Boolean {
//        return stmt is Throws && arg == stmt.arg
//    }
}

