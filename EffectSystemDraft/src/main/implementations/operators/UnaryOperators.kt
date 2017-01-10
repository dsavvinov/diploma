package main.implementations.operators

import main.lang.Exception
import main.structure.Operator
import main.structure.UnaryOperator
import main.structure.Visitor

class Throws(override val arg: Exception) : UnaryOperator {
    override fun accept(visitor: Visitor): Throws {
        return visitor.visit(this)
    }

    override fun isImplies(op: Operator): Boolean {
        return op is Throws && arg == op.arg
    }
}

