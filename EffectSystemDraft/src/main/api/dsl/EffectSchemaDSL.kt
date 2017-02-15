package main.api.dsl

import main.implementations.EffectImpl
import main.implementations.EffectSchemaImpl
import main.structure.general.EsFunction
import main.structure.general.EsNode
import main.structure.schema.Effect
import main.structure.schema.EffectSchema

class EffectSchemaBuilder(val function: EsFunction) {
    val effects: MutableList<Effect> = mutableListOf()
    val returnVar = function.returnVar

    infix fun (EsNode).to(other: EsNode): Unit {
        effects += EffectImpl(this, other)
    }

    fun build(): EffectSchema {
        return EffectSchemaImpl(function, function.returnVar, effects)
    }
}

fun EsFunction.defineSchema(description: (EffectSchemaBuilder).() -> Unit): EffectSchema {
    val effectSchema = EffectSchemaBuilder(this).apply { description() }.build()
    this.schema = effectSchema
    return effectSchema
}