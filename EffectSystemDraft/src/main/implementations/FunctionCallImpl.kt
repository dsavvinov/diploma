package main.implementations

import main.structure.Function
import main.structure.FunctionCall
import main.structure.Node
import main.structure.Visitor

data class FunctionCallImpl(override val function: Function, override val args: List<Node>) : FunctionCall {
    override fun accept(visitor: Visitor): Node {
        return visitor.visit(this)
    }
}