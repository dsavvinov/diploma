package main.lang

/**
 * All classes in this file represent simplified abstraction
 * over Kotlin language nodes and structures;
 *
 * It is used for development and prototyping purposes
 * and should be replaced with real Kotlin-classes in future.
 */


interface LangNode {
    fun <T> accept(visitor: CallTreeVisitor<T>): T
}

interface FunctionCall : LangNode {
    val function: Function
    val args: List<LangNode>

    override fun <T> accept(visitor: CallTreeVisitor<T>): T = visitor.visit(this)
}

interface EqualOperator : LangNode {
    val left: LangNode
    val right: LangNode

    override fun <T> accept(visitor: CallTreeVisitor<T>): T = visitor.visit(this)
}

interface IsOperator : LangNode {
    val left: LangNode
    val right: LangNode

    override fun <T> accept(visitor: CallTreeVisitor<T>): T = visitor.visit(this)
}

interface Variable : LangNode {
    val name: String
    val type: Type

    override fun <T> accept(visitor: CallTreeVisitor<T>): T = visitor.visit(this)
}

interface Constant : LangNode {
    val value: Any
    val type: Type

    override fun <T> accept(visitor: CallTreeVisitor<T>): T = visitor.visit(this)
}
