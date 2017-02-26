package main.structure.schema.effects

import main.structure.schema.Effect
import main.structure.schema.SchemaVisitor
import main.structure.schema.operators.BinaryOperator

// Tag-type for distinguishing
interface Outcome : Effect {
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
    fun followsFrom(other: Outcome): Boolean
}

interface SimpleEffect : Effect {
    fun isCombinable(effect: Effect) : Boolean
    fun merge(right: SimpleEffect) : SimpleEffect

    override fun merge(left: List<Effect>, right: List<Effect>, flags: EffectsPipelineFlags, operator: BinaryOperator): List<Effect> {
        if (flags.isVetoed()) {
            return listOf()
        }

        val leftCombinable = left.filter { isCombinable(it) }
        val rightCombinable = right.filter { isCombinable(it) }

        // Part of SimpleEffect-Contract
        assert(leftCombinable.size == 1)
        assert(rightCombinable.size == 1)

        return listOf(
                (leftCombinable[0] as SimpleEffect).merge(rightCombinable[0] as SimpleEffect)
        )
    }

    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
}

fun (List<Effect>).merge(right: List<Effect>, operator: BinaryOperator) : List<Effect> {
    val flags = EffectsPipelineFlags()
    return flatMap { it.merge(this, right, flags, operator) }
}