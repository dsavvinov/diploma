package main.structure

import main.lang.KtType

data class Is(val left: EsNode, val right: KtType) : Operator {
    override fun <T> accept(operatorsVisitor: OperatorsVisitor<T>): T = operatorsVisitor.visit(this)
}

data class Equal(val left: EsNode, val right: EsNode) : Operator {
    override fun <T> accept(operatorsVisitor: OperatorsVisitor<T>): T = operatorsVisitor.visit(this)
}

data class Throws(val exception: Any?) : Operator {
    override fun <T> accept(operatorsVisitor: OperatorsVisitor<T>): T = operatorsVisitor.visit(this)
}

data class Or(val left: EsNode, val right: EsNode) : Operator {
    override fun <T> accept(operatorsVisitor: OperatorsVisitor<T>): T = operatorsVisitor.visit(this)
}

data class And(val left: EsNode, val right: EsNode) : Operator {
    override fun <T> accept(operatorsVisitor: OperatorsVisitor<T>): T = operatorsVisitor.visit(this)
}

data class Not(val arg: EsNode) : Operator {
    override fun <T> accept(operatorsVisitor: OperatorsVisitor<T>): T = operatorsVisitor.visit(this)
}