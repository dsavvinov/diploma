package main.api.dsl

import main.structure.general.EsFunction
import main.structure.general.EsNode
import main.structure.schema.EffectSchema
import main.structure.schema.operators.Imply

class EffectSchemaBuilder {
    val clauses: MutableList<Imply> = mutableListOf()

    infix fun (EsNode).to(other: EsNode): Unit {
        clauses += Imply(this, other)
    }

    fun build(): EffectSchema {
        return EffectSchema(clauses)
    }
}

fun EsFunction.defineSchema(description: (EffectSchemaBuilder).() -> Unit): EffectSchema {
    val effectSchema = EffectSchemaBuilder().apply { description() }.build()
    this.schema = effectSchema
    return effectSchema
}