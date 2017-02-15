package main.structure.schema.operators

import main.implementations.EffectImpl
import main.implementations.EffectSchemaImpl
import main.implementations.general.EsConstantImpl
import main.implementations.visitors.transform
import main.structure.EsBoolean
import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsType
import main.structure.general.EsVariable
import main.structure.lift
import main.structure.schema.Effect
import main.structure.schema.EffectSchema
import main.structure.schema.Returns
import main.structure.schema.SchemaVisitor

data class Is(val left: EsNode, val right: EsType) : EsNode {
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
}

fun (EffectSchema).evaluateIs(type: EsType) : EffectSchema {
    val evaluatedEffects = mutableListOf<Effect>()
    for (effect: Effect in effects) {
        val rewritedConclusion = effect.conclusion.transform {
            // Skip all non-Returns staments
            if (it !is Returns) {
                return@transform it
            }

            // If we don't know return-type then set return-value of Is-operator to unknown
            if (it.type == null) {
                return@transform Returns(null, null)
            }

            // Otherwise evalaute Is-operator and update Returns-clause accordingly
            return@transform Returns((it.type == type).lift(), EsBoolean)
        }

        evaluatedEffects.add(EffectImpl(effect.premise, rewritedConclusion))
    }
    return EffectSchemaImpl(function, returnVar, evaluatedEffects)
}

fun (EsVariable).evaluateIs(rhsType: EsType) : EsNode = (this.type == rhsType).lift()

fun (EsConstant).evaluateIs(rhsType: EsType) : EsNode = (this.type == rhsType).lift()