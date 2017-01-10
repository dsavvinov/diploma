package main.structure

import main.implementations.operators.*
import main.lang.*
import main.lang.Function
import main.structure.system.*

// TODO: think about selective visitor
// TODO: returns Data, where Data is generic arg
interface Visitor<out T> {
    fun visit(schema: EffectSchema): T
    fun visit(effect: Effect): T

    fun visit(isOperator: Is): T
    fun visit(equalOperator: Equal): T
    fun visit(throwsOperator: Throws): T

    fun visit(or: Or): T
    fun visit(and: And): T
    fun visit(not: Not): T

    fun visit(variable: Variable): T
    fun visit(constant: Constant): T
}
