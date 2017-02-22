package main.structure.schema.effects

import main.structure.general.EsFunction
import main.structure.schema.Effect
import main.structure.schema.SchemaVisitor

data class Calls(val callCounts: MutableMap<EsFunction, Int>) : SimpleEffect {
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)

    override fun isCombinable(effect: Effect): Boolean = effect is Calls

    override fun merge(right: SimpleEffect): Calls {
        val resultCalls = mutableMapOf<EsFunction, Int>()
        resultCalls.putAll(callCounts)
        for ((function, calls) in (right as Calls).callCounts) {
            resultCalls.merge(function, calls, Int::plus)
        }

        return Calls(resultCalls)
    }
}