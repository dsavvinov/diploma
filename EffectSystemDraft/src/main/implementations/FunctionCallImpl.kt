package main.implementations

import main.structure.*
import main.structure.Function

data class FunctionCallImpl(override val function: Function, override val args: List<Node>) : FunctionCall {
    override fun accept(visitor: Visitor): Node {
        return visitor.visit(this)
    }

    override fun isImplies(op: Operator): Boolean {
        return false
    }
}