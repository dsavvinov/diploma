package main.structure.schema.effects

import main.facade.Unknown
import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsType
import main.structure.general.EsVariable
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

    override fun followsFrom(other: Outcome): Boolean {
        if (other !is Returns) return false

        // Returns(Unknown) conforms to any kind of Returns
        if (value == Unknown) return true

        // Variable can take any value, so anything follows from such Returns
        if (other.value is EsVariable) return true

        // If both Returns have a constant as their arg, then just compare them
        if (other.value is EsConstant && this.value is EsConstant) {
            return value == other.value
        }

        // Default case - conservatively assume that nothing conforms
        return false
    }
}
