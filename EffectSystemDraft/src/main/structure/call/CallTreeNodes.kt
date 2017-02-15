package main.structure.call

import main.structure.general.*

interface CtNode {
    fun <T> accept(visitor: CallTreeVisitor<T>): T = visitor.visit(this)
}

interface CtCall : CtNode {
    val function: EsFunction
    val childs: List<CtNode>

    override fun <T> accept(visitor: CallTreeVisitor<T>): T = visitor.visit(this)
}