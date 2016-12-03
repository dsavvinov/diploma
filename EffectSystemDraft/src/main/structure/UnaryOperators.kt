package main.structure

class Throws(override val arg: Exception) : UnaryOperator {
    override fun accept(visitor: Visitor): Throws {
        return visitor.visit(this)
    }
}

