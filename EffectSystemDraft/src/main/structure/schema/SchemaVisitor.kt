package main.structure.schema

import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsType
import main.structure.general.EsVariable
import main.structure.schema.effects.*
import main.structure.schema.operators.*

// TODO: think about selective visitor
interface SchemaVisitor<out T> {

    // Generic nodes
    fun visit(node: EsNode): T = throw IllegalStateException("Top-level of nodes hierarchy reached, no overloads found")
    fun visit(schema: EffectSchema): T = visit(schema as EsNode)
    fun visit(variable: EsVariable): T = visit(variable as EsNode)
    fun visit(constant: EsConstant): T = visit(constant as EsNode)
    fun visit(type: EsType): T = visit(type as EsNode)


    // Operators
    fun visit(operator: Operator): T = visit(operator as EsNode)

    fun visit(binaryOperator: BinaryOperator): T = visit(binaryOperator as Operator)
    fun visit(unaryOperator: UnaryOperator): T = visit(unaryOperator as Operator)

    fun visit(isOperator: Is): T = visit(isOperator as BinaryOperator)
    fun visit(equalOperator: Equal): T = visit(equalOperator as BinaryOperator)
    fun visit(imply: Imply): T = visit(imply as BinaryOperator)

    fun visit(or: Or): T = visit(or as BinaryOperator)
    fun visit(and: And): T = visit(and as BinaryOperator)
    fun visit(not: Not): T = visit(not as UnaryOperator)


    // Effects
    fun visit(effect: Effect): T = visit(effect as EsNode)

    fun visit(simpleEffect: SimpleEffect): T = visit(simpleEffect as Effect)
    fun visit(outcome: Outcome): T = visit(outcome as Effect)

    fun visit(returns: Returns): T = visit(returns as Outcome)
    fun visit(throws: Throws): T = visit(throws as Outcome)

    fun visit(calls: Calls): T = visit(calls as SimpleEffect)
}
