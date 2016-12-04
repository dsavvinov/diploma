package main.structure

import main.implementations.visitors.Printer

interface Node {
    fun accept(visitor: Visitor): Node
    fun print(): String {
        val printer = Printer()
        accept(printer)
        return printer.toString()
    }
}

// TODO: think if we can (and if we should) be more accurate and express, that
// args can't be any Node (introduce notion of expressions?)
interface FunctionCall : Node {
    val function: Function
    val args: List<Node>
}

interface EffectSchema : Node {
    val function: Function
    val effects: List<Effect>
    override fun accept(visitor: Visitor): EffectSchema
}

interface Effect : Node {
    val premise: LogicStatement
    val conclusion: LogicStatement

    override fun accept(visitor: Visitor): Effect
}


// Something, than can be evaluated to TRUE or FALSE
interface LogicStatement : Node {
    override fun accept(visitor: Visitor): LogicStatement

    fun isImplies(stmt: LogicStatement): Boolean
}

interface BinaryOperator : LogicStatement {
    val left: Node
    val right: Node
}

interface UnaryOperator : LogicStatement {
    val arg: Node
}

interface LogicBinaryOperator : LogicStatement, BinaryOperator {
    override val left: LogicStatement
    override val right: LogicStatement
}

interface LogicUnaryOperator : LogicStatement, UnaryOperator {
    override val arg: LogicStatement
}

// Leafs of our tree-like structure
interface Primitive : Node