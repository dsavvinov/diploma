package main.api

import main.implementations.visitors.Evaluator
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

    fun inferEffects(call: FunctionCall): EffectSchema {
        val evaluator = Evaluator(mutableMapOf())
        val result = evaluator.visit(call)
        return result
    }
}

