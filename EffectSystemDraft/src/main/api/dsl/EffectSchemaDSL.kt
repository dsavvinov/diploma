package main.api.dsl

import main.implementations.ClauseImpl
import main.implementations.EffectSchemaImpl
import main.structure.general.EsFunction
import main.structure.general.EsNode
import main.structure.schema.Clause
import main.structure.schema.EffectSchema

class EffectSchemaBuilder(val function: EsFunction) {
    val clauses: MutableList<Clause> = mutableListOf()
    val returnVar = function.returnVar

    infix fun (EsNode).to(other: EsNode): Unit {
        clauses += ClauseImpl(this, other)
    }

    fun build(): EffectSchema {
        return EffectSchemaImpl(clauses)
    }
}

fun EsFunction.defineSchema(description: (EffectSchemaBuilder).() -> Unit): EffectSchema {
    val effectSchema = EffectSchemaBuilder(this).apply { description() }.build()
    this.schema = effectSchema
    return effectSchema
}