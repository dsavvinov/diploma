package main.structure.schema.operators

import main.structure.general.EsNode
import main.structure.schema.EffectSchema
import main.structure.schema.Operator
import main.structure.schema.SchemaVisitor
import main.structure.schema.Term
import main.structure.schema.effects.EffectsPipelineFlags
import main.structure.schema.effects.Returns
import main.visitors.helpers.transformReturn

interface UnaryOperator : Operator {
    val arg: EsNode

    // 'Prototype' pattern
    fun newInstance(arg: EsNode) : UnaryOperator

    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)

    override fun flatten(): EsNode {
        if (arg !is EffectSchema) {
            // Nothing to do, move on
            return this
        }

        val schema = arg as EffectSchema
        val newClauses = schema.clauses.map {
            it.transformReturn { Returns(newInstance(it.value), it.type) }
        }

        return EffectSchema(newClauses)
    }
}

interface BinaryOperator : Operator {
    val left: EsNode
    val right: EsNode

    // 'Prototype' pattern
    fun newInstance(left: EsNode, right: EsNode) : BinaryOperator

    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)

    override fun flatten(): EsNode {
        if (left !is Term || right !is Term) return this

        val leftSchema: EffectSchema = (left as Term).castToSchema()
        val rightSchema: EffectSchema = (right as Term).castToSchema()

        return leftSchema.flattenWith(rightSchema, operator = this)
    }
}

private fun (EffectSchema).flattenWith(rightSchema: EffectSchema, operator: BinaryOperator): EffectSchema {
    val newClauses = rightSchema.clauses.flatMap { rightClause ->
        this.clauses.map { leftClause ->
            val flags = EffectsPipelineFlags()
            val left = leftClause.left.and(rightClause.left)
            val right = leftClause.effectsAsList().flatMap {
                it.merge(leftClause.effectsAsList(), rightClause.effectsAsList(), flags, operator)
            }
            Imply(left, right)
        }
    }
    return EffectSchema(newClauses)
}