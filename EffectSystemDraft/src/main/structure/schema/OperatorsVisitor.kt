package main.structure

interface OperatorsVisitor<out T> {
    fun visit(isOperator: Is): T
    fun visit(equalOperator: Equal): T
    fun visit(throwsOperator: Throws): T

    fun visit(or: Or): T
    fun visit(and: And): T
    fun visit(not: Not): T
}