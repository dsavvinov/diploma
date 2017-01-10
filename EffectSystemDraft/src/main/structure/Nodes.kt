package main.structure

import main.lang.KtVariable

interface Node {
    fun <T> accept(visitor: SchemaVisitor<T>): T
}

interface EffectSchema : Node {
    val returnVar: Variable
    val effects: List<Effect>
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
}

interface Effect : Node {
    val premise: Node
    val conclusion: Node
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
}

interface Operator : Node {
    fun <T> accept(operatorsVisitor: OperatorsVisitor<T>): T
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
}

interface Variable : Node, KtVariable {
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
}