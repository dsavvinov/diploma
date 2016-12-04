package main.api.dsl

import main.implementations.EffectImpl
import main.implementations.EffectSchemaImpl
import main.structure.*
import main.structure.Function

class EffectSchemaBuilder(val function: Function) {
    val effects: MutableList<Effect> = mutableListOf()
    val returnVar = function.returnVar

    infix fun (LogicStatement).to(other: LogicStatement): Unit {
        effects += EffectImpl(this, other)
    }

    fun build(): EffectSchema {
        return EffectSchemaImpl(function, effects)
    }

}