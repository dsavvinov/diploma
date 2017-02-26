package main.structure.call

import main.structure.general.*

/**
 * Represents Call-Tree structure.
 *
 * Call-Tree is built from AST that represents particular call.
 */
interface CtNode {
    fun <T> accept(visitor: CallTreeVisitor<T>): T = visitor.visit(this)
}

data class CtCall(val function: EsFunction, val childs: List<CtNode>) : CtNode {
    override fun <T> accept(visitor: CallTreeVisitor<T>): T = visitor.visit(this)
}