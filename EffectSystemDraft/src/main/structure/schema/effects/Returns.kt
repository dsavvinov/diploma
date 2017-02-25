package main.structure.schema.effects

import main.structure.general.EsNode
import main.structure.general.EsType
import main.structure.schema.Effect
import main.structure.schema.SchemaVisitor
import main.structure.schema.operators.BinaryOperator

data class Returns(val value: EsNode, val type: EsType?) : Outcome {
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)

    override fun merge(left: List<Effect>, right: List<Effect>, flags: EffectsPipelineFlags, operator: BinaryOperator): List<Effect> {
        val rightOutcome = right.find { it is Outcome }

        return when(rightOutcome) {
            is Throws -> listOf(rightOutcome)
            is Returns -> listOf(Returns(operator.newInstance(this.value, rightOutcome.value), type))
            // TODO: mb sealed class here?
            else -> throw IllegalStateException("Unexpected Outcome-type: $rightOutcome")
        }
    }


}
