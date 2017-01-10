package main.lang

import main.structure.Node

/**
 * All classes in this file represent simplified abstraction
 * over Kotlin language nodes and structures;
 *
 * It is used for development and prototyping purposes
 * and should be replaced with real Kotlin-classes in future.
 */

interface KtNode {
    fun <T> accept(visitor: CallTreeVisitor<T>): T
}

interface FunctionCall : KtNode {
    val function: Function
    val args: List<KtNode>
    override fun <T> accept(visitor: CallTreeVisitor<T>): T = visitor.visit(this)
}

interface IsOperator : KtNode {
    val left: KtNode
    val right: KtNode
    override fun <T> accept(visitor: CallTreeVisitor<T>): T = visitor.visit(this)
}

interface EqualOperator : KtNode {
    val left: KtNode
    val right: KtNode
    override fun <T> accept(visitor: CallTreeVisitor<T>): T = visitor.visit(this)
}

interface KtVariable : KtNode {
    val name: String
    val type: Type
    override fun <T> accept(visitor: CallTreeVisitor<T>): T = visitor.visit(this)
}