package main.structure.schema

import main.implementations.visitors.helpers.filter
import main.implementations.visitors.helpers.toList
import main.structure.general.EsNode
import main.structure.schema.effects.EffectsPipelineFlags
import main.structure.schema.operators.BinaryOperator


interface EffectSchema : EsNode {
    val clauses: List<EsNode>
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
}

interface Effect : EsNode {
    fun merge(left: List<Effect>, right: List<Effect>, flags: EffectsPipelineFlags, operator: BinaryOperator) : List<Effect>
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
}

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