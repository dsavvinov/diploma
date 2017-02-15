package main.structure.schema

import main.structure.schema.operators.Equal
import main.structure.schema.operators.Is

interface OperatorsVisitor<out T> {
    fun visit(isOperator: Is): T
    fun visit(equalOperator: Equal): T
    fun visit(throwsOperator: Throws): T

    fun visit(or: Or): T
    fun visit(and: And): T
    fun visit(not: Not): T
}