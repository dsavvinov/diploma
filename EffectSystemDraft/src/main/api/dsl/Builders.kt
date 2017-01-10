package main.api.dsl

import main.implementations.EffectImpl
import main.implementations.EffectSchemaImpl
import main.structure.*
import main.lang.Function
import main.structure.Effect
import main.structure.EffectSchema
import main.structure.Node

class EffectSchemaBuilder(val function: Function) {
    val effects: MutableList<Effect> = mutableListOf()
    val returnVar = function.returnVar

    infix fun (Node).to(other: Node): Unit {
        effects += EffectImpl(this, other)
    }

    fun build(): EffectSchema {
        return EffectSchemaImpl(effects, function.returnVar)
    }

}