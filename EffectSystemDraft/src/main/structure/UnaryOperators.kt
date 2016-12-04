package main.structure

class Throws(override val arg: Exception) : UnaryOperator {
    override fun accept(visitor: Visitor): Throws {
        return visitor.visit(this)
    }

    override fun isImplies(op: Operator): Boolean {
        return op is Throws && arg == op.arg
    }
}

