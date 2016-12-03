package main.api

import main.structure.EffectSchema
import main.structure.Function
import main.structure.FunctionCall

object EffectSystem {
    private val schemas: MutableMap<Function, EffectSchema> = mutableMapOf()

    fun addEffectSchema(function: Function, es: EffectSchema) {
        schemas[function] = es
    }

    fun getEffectSchema(function: Function): EffectSchema {
        return schemas[function]!!
    }

    // TODO: think about return value
    fun inferEffects(call: FunctionCall): Any {
        call.evaluate()
        return TODO()
    }
}

