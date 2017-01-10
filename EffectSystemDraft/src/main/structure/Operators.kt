package main.structure

import main.lang.Type

data class Is(val left: Node, val right: Type) : Operator {
    override fun <T> accept(operatorsVisitor: OperatorsVisitor<T>): T = operatorsVisitor.visit(this)
}

data class Equal(val left: Node, val right: Node) : Operator {
    override fun <T> accept(operatorsVisitor: OperatorsVisitor<T>): T = operatorsVisitor.visit(this)
}

data class Throws(val exception: Any?) : Operator {
    override fun <T> accept(operatorsVisitor: OperatorsVisitor<T>): T = operatorsVisitor.visit(this)
}

data class Or(val left: Node, val right: Node) : Operator {
    override fun <T> accept(operatorsVisitor: OperatorsVisitor<T>): T = operatorsVisitor.visit(this)
}

data class And(val left: Node, val right: Node) : Operator {
    override fun <T> accept(operatorsVisitor: OperatorsVisitor<T>): T = operatorsVisitor.visit(this)
}

data class Not(val arg: Node) : Operator {
    override fun <T> accept(operatorsVisitor: OperatorsVisitor<T>): T = operatorsVisitor.visit(this)
}