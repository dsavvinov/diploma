package main.structure.schema.operators

import main.implementations.EffectSchemaImpl
import main.implementations.visitors.and
import main.implementations.visitors.helpers.print
import main.implementations.visitors.helpers.transformReturn
import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsType
import main.structure.general.EsVariable
import main.structure.lift
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
            (it as Imply).transformReturn { Returns(newInstance(it.value), it.type) }
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
        val leftSchema: EffectSchema = left.castToSchema()
        val rightSchema: EffectSchema = right.castToSchema()

        return leftSchema.flattenWith(rightSchema, operator = this)
    }
}

fun (EsNode).castToSchema(): EffectSchema {
    return when (this) {
        is EffectSchema -> this
        is EsVariable -> this.castToSchema()
        is EsConstant -> this.castToSchema()
        else -> throw IllegalArgumentException("Type doesn't support casting to ES: $this")
    }
}

private fun (EffectSchema).flattenWith(rightSchema: EffectSchema, operator: BinaryOperator): EffectSchema {
//    println("Flattening:")
//    println(this.print())
//    println("and")
//    println(rightSchema.print())

    val newClauses = rightSchema.clauses.flatMap { rightClause ->
        this.clauses.map { leftClause ->
            val flags = EffectsPipelineFlags()
            val left = (leftClause as Imply).left.and((rightClause as Imply).left)
            val right = leftClause.effectsAsList().flatMap {
                it.merge(leftClause.effectsAsList(), rightClause.effectsAsList(), flags, operator)
            }
            Imply(left, right)
        }
    }

    val result = EffectSchemaImpl(newClauses)
//    println("result:")
//    println(result.print())
//    println("================")
//    println()

    return EffectSchemaImpl(newClauses)
}