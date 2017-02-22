package main.structure.schema.operators

import main.implementations.ClauseImpl
import main.implementations.EffectSchemaImpl
import main.implementations.visitors.and
import main.implementations.visitors.getOutcome
import main.implementations.visitors.removeOutcome
import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsVariable
import main.structure.lift
import main.structure.schema.Clause
import main.structure.schema.EffectSchema
import main.structure.schema.SchemaVisitor
import main.structure.schema.effects.EffectsPipelineFlags
import main.structure.schema.effects.Returns
import main.structure.schema.effects.merge

data class Imply(override val left: EsNode, override val right: EsNode) : BinaryOperator {
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
    override fun newInstance(left: EsNode, right: EsNode): BinaryOperator = Imply(left, right)

    override fun flatten(): EsNode {
        return when(left) {
            is EffectSchema -> left.flattenImply(right)
            is EsVariable -> left.castToSchema().flattenImply(right)
            is EsConstant -> left.castToSchema().flattenImply(right)
            else -> throw IllegalStateException("Unknown left-type for Imply-operator: $left")
        }
    }

    override fun reduce(): EsNode {
        if (left == true.lift()) {
            return right
        }
        return this
    }

    private fun (EffectSchema).flattenImply(right: EsNode): EffectSchema {
        return when (right) {
            is EffectSchema -> flattenImply(right)
            is EsVariable -> flattenImply(right.castToSchema())
            is EsConstant -> flattenImply(right.castToSchema())
            else -> throw IllegalStateException("Unknown left-type for Imply-operator: $left")
        }
    }

    private fun (EffectSchema).flattenImply(right: EffectSchema): EffectSchema {
        val resultedClauses = mutableListOf<Clause>()
        for (leftClause in clauses) {
            val outcome = leftClause.getOutcome()

            // Add all false-resulting clauses without implying rhs
            if (outcome is Returns && outcome.value == false.lift()) {
                resultedClauses.add(leftClause.removeOutcome())
            }

            // Otherwise, use usual flattening for binary operators:
            val flags = EffectsPipelineFlags()
            for (rightClause in right.clauses) {
                val premise = leftClause.premise.and(rightClause.premise)
                val conclusion = leftClause.effectsAsList().merge(rightClause.effectsAsList(), this@Imply)
                resultedClauses.add(ClauseImpl(premise, conclusion))
            }
        }
        return EffectSchemaImpl(resultedClauses)
    }
}