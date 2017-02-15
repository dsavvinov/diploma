package main.structure.schema

import main.structure.general.EsConstant
import main.structure.general.EsType
import main.structure.general.EsVariable
import main.structure.schema.operators.Equal
import main.structure.schema.operators.Is

// TODO: think about selective visitor
// TODO: returns Data, where Data is generic arg
interface SchemaVisitor<out T> {
    fun visit(schema: EffectSchema): T

    fun visit(effect: Effect): T

    fun visit(variable: EsVariable): T

    fun visit(constant: EsConstant): T

    fun visit(type: EsType): T

    fun visit(isOp: Is): T

    fun visit(equalOp: Equal): T

    fun visit(throwsOp: Throws): T

    fun visit(orOp: Or): T

    fun visit(andOp: And): T

    fun visit(notOp: Not): T

    fun visit(returns: Returns): T
}
