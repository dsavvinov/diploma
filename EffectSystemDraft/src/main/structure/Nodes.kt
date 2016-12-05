package main.structure

import main.implementations.visitors.Printer

interface Node {
    fun accept(visitor: Visitor): Node
    // TODO: refactor in extension function
    fun print(): String {
        val printer = Printer()
        accept(printer)
        return printer.toString()
    }

    // TODO: delete! We have Searcher!
    fun isImplies(op: Operator): Boolean
}

// TODO: think if we can (and if we should) be more accurate and express, that
// args can't be any Node (introduce notion of expressions?)
interface FunctionCall : Node {
    val function: Function
    val args: List<Node>
}

interface EffectSchema : Node {
    val returnVar: Variable
    val effects: List<Effect>
    override fun accept(visitor: Visitor): EffectSchema
}

interface Effect : Node {
    val premise: Node
    val conclusion: Node

    override fun accept(visitor: Visitor): Node
}

interface Operator : Node

interface BinaryOperator : Operator {
    val left: Node
    val right: Node
}

interface UnaryOperator : Operator {
    val arg: Node
}

// Leafs of our tree-like structure
// TODO: think if we really need this (we don't use it as Primitive
// TODO: introduce equal-like operator for equality (not `equal()`)
interface Primitive : Node