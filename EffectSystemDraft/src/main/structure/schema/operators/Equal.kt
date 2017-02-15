package main.structure.schema.operators

import main.implementations.EffectSchemaImpl
import main.implementations.visitors.getOutcome
import main.implementations.visitors.transform
import main.structure.EsBoolean
import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsVariable
import main.structure.lift
import main.structure.schema.Effect
import main.structure.schema.EffectSchema
import main.structure.schema.Returns
import main.structure.schema.SchemaVisitor

data class Equal(val left: EsNode, val right: EsNode) : EsNode {
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
}

fun (EffectSchema).evaluateEqual(rhs: EsNode) : EsNode {
    return when (rhs) {
        is EffectSchema -> evaluateEqual(rhs)
        is EsVariable -> evaluateEqual(rhs)
        is EsConstant -> evaluateEqual(rhs)
        else -> throw IllegalStateException("Unknown rhs for Equal-clause: $rhs")
    }
}

fun (EffectSchema).evaluateEqual(rhs: EsVariable) : EsNode {
    TODO()
}

fun (EffectSchema).evaluateEqual(rhs: EsConstant) : EsNode {
    val resultEffects = mutableListOf<Effect>()
    effects.forEach { effect ->
        val outcome = effect.getOutcome()
        if (outcome is Returns) {
            // Change return-clause of effect
            val modifiedEffect = effect.transform { node ->
                if (node !is Returns) return@transform node
                val tmp1 = node.value
                val tmp2 = rhs
                val tmp3 = node.value == rhs
                return@transform Returns((node.value == rhs).lift(), EsBoolean)
            } as Effect
            resultEffects.add(modifiedEffect)
        } else {
            // Otherwise, leave current outcome unchanged
            resultEffects.add(effect)
        }
    }
    return EffectSchemaImpl(function, returnVar, resultEffects)
}

fun (EffectSchema).evaluateEqual(rhs: EffectSchema) : EsNode {
    TODO()
}

fun (EsConstant).evaluateEqual(rhs: EsNode) : EsNode {
    return when(rhs) {
        is EffectSchema -> evaluateEqual(rhs)
        is EsConstant -> evaluateEqual(rhs)
        is EsVariable -> evaluateEqual(rhs)
        else -> throw IllegalStateException()
    }
}
fun (EsConstant).evaluateEqual(rhs: EffectSchema) : EsNode = rhs.evaluateEqual(this)

fun (EsConstant).evaluateEqual(rhs : EsVariable) : EsNode = TODO()

fun (EsConstant).evaluateEqual(rhs : EsConstant) : EsNode = (value == rhs.value).lift()