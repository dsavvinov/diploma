package system

/**
 * Created by dsavvinov on 25.11.16.
 */

object Context {
    private val function2effectSchema: MutableMap<Function, EffectSchema> = mutableMapOf()

    fun getEffectSchema(function: Function): EffectSchema {
        return function2effectSchema[function] ?: throw IllegalArgumentException("system.Function $function not defined!")
    }

    fun addEffectSchema(function: Function, effectSchema: EffectSchema) {
        function2effectSchema[function] = effectSchema
    }
}