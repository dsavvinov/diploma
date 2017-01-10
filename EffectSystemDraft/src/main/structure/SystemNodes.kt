package main.structure

import main.lang.Variable

interface Node {
    fun <T> accept(visitor: Visitor<T>): T
}

interface EffectSchema : Node {
    val returnVar: Variable
    val effects: List<Effect>
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visit(this)
}

interface Effect : Node {
    val premise: Node
    val conclusion: Node
    override fun <T> accept(visitor: Visitor<T>): T = visitor.visit(this)
}

interface Operator : Node

interface BinaryOperator : Operator {
    val left: Node
    val right: Node
}

interface UnaryOperator : Operator {
    val arg: Node
}