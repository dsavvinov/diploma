package main.structure.schema.effects

import main.structure.schema.Effect
import main.structure.schema.SchemaVisitor
import main.structure.schema.operators.BinaryOperator

data class Throws(val exception: Any?) : Outcome {
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)

    override fun merge(left: List<Effect>, right: List<Effect>, flags: EffectsPipelineFlags, operator: BinaryOperator): List<Effect> {
        // Shutdown pipeline, and override result with left-effects.
        flags.veto()
        return left
    }

    override fun followsFrom(other: Outcome): Boolean {
        if (other !is Throws) return false

        // TODO: insert proper subtyping here
        return exception == other.exception
    }
}