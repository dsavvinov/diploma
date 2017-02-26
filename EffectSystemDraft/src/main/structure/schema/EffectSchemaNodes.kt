package main.structure.schema

import main.structure.general.EsNode
import main.structure.schema.effects.EffectsPipelineFlags
import main.structure.schema.operators.BinaryOperator
import main.structure.schema.operators.Imply

/**
 * General type of any side-effects that any computation may have.
 */
interface Effect : EsNode {
    /**
     * Returns result of combination of `this`-effect with `right`-Effects.
     *
     * `this` guaranteed to be present in `left`.
     *
     * This is most general signature of `merge()`-function. See `SimpleEffect`
     * for more convenient and special case.
     */
    fun merge(left: List<Effect>, right: List<Effect>, flags: EffectsPipelineFlags, operator: BinaryOperator) : List<Effect>
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
}

/**
 * General type of an operation that knows how to flatten if one of its arguemnts is EffectSchema
 */
interface Operator : EsNode {
    /**
     * Merges arguments in EffectSchema if at least one of them was EffectSchema.
     * Otherwise, returns `this` unchanged
     */
    fun flatten(): EsNode

    /**
     * Tries to evaluate operator on its arguments, returning `this` unchanged if evaluation is impossible.
     */
    fun reduce(): EsNode

    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
}

/**
 * Term is an irreducible EsNode. All terms should be equivalent to some form of EffectSchema
 */
interface Term : EsNode {
   fun castToSchema(): EffectSchema
}

data class EffectSchema(val clauses: List<Imply>) : Term {
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
    override fun castToSchema(): EffectSchema = this
}

