package main.structure.schema.operators

import main.implementations.ClauseImpl
import main.implementations.EffectSchemaImpl
import main.implementations.visitors.and
import main.implementations.visitors.helpers.transformReturn
import main.structure.general.EsNode
import main.structure.schema.EffectSchema
import main.structure.schema.Operator
import main.structure.schema.SchemaVisitor
import main.structure.schema.effects.EffectsPipelineFlags
import main.structure.schema.effects.Returns

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

        return EffectSchemaImpl(newClauses)
    }
}

interface BinaryOperator : Operator {
    val left: EsNode
    val right: EsNode

    // 'Prototype' pattern
    fun newInstance(left: EsNode, right: EsNode) : BinaryOperator

    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)

    override fun flatten(): EsNode {
        return when (left) {
            is EffectSchema -> {
                val leftSchema = left as EffectSchema
                if (right is EffectSchema) {
                    // TODO: nice example of where effects can be used!
                    val rightSchema = right as EffectSchema

                    val newClauses = rightSchema.clauses.flatMap { rightClause ->
                        leftSchema.clauses.map { leftClause ->
                            val flags = EffectsPipelineFlags()
                            ClauseImpl(
                                    leftClause.premise.and(rightClause.premise),
                                    leftClause.effectsAsList().flatMap {
                                        it.merge(leftClause.effectsAsList(), rightClause.effectsAsList(), flags, this)
                                    }
                            )
                        }
                    }

                    EffectSchemaImpl(newClauses)
                } else {
                  TODO()
                }
            }

            else -> TODO()
        }
    }
}
