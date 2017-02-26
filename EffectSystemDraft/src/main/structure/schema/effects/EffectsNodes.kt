package main.structure.schema.effects

import main.structure.schema.Effect
import main.structure.schema.SchemaVisitor
import main.structure.schema.operators.BinaryOperator

/**
 * Special subclass of effects, that represents particular outcome of
 * the corresponding code block.
 */
interface Outcome : Effect {
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
    fun followsFrom(other: Outcome): Boolean
}

/**
 * Simple Effect is a subclass of effects that doesn't influence other effects
 * on combining. Therefore, Simple Effects have more simple signature of `merge()`
 */
interface SimpleEffect : Effect {
    /**
     * Template method.
     * Should return `true` iff `effect` can be combined with `this`.
     * In most cases, for class X : SimpleEffect this should be implemented
     * as `fun isCombinable(effect: Effect) = effect is X`
     */
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