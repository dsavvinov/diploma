package main.structure.schema.operators

import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsVariable
import main.structure.lift
import main.structure.schema.Effect
import main.structure.schema.EffectSchema
import main.structure.schema.SchemaVisitor
import main.structure.schema.effects.Returns
import main.visitors.helpers.filter
import main.visitors.helpers.getOutcome
import main.visitors.helpers.removeReturns
import main.visitors.helpers.toList

/**
 * Equivalent of logic implication in context of Effect System.
 */
data class Imply(override val left: EsNode, override val right: EsNode) : BinaryOperator {
    constructor(premise:EsNode, effects: List<Effect>) : this(
            premise,
            effects.reduceRight<EsNode, Effect> { effect, acc -> acc.and(effect) }
    )

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

    override fun reduce(): EsNode = this




    private fun (EffectSchema).flattenImply(right: EsNode): EffectSchema {
        return when (right) {
            is EffectSchema -> flattenImply(right)
            else -> flattenImply(EffectSchema(listOf(Imply(true.lift(), right))))
        }
    }

    private fun (EffectSchema).flattenImply(right: EffectSchema): EffectSchema {
        val resultedClauses = mutableListOf<Imply>()
        for (leftClause in clauses) {
            val outcome = leftClause.getOutcome()

            if (outcome !is Returns) {
                // Add non-successfull clause as is
                resultedClauses.add(leftClause)
                continue
            }

            val lClauseWithoutReturns = leftClause.removeReturns()

            // Now, as we know that left part is finishing and returns something,
            // we can add additional condition which will be true iff leftClause returned true
            val cond = Equal(outcome.value, true.lift())

            for (rightClause in right.clauses) {
                // Form the premise for combined clause: leftClause.left && rightClause.left && cond
                val premise = leftClause.left.and(rightClause.left).and(cond)

                // Form the conclusion for combined clause:
                //      leftClause.right.removeOutcome() && rightClause.right
                val conclusion = lClauseWithoutReturns.right.and(rightClause.right)
                resultedClauses.add(Imply(premise, conclusion))
            }
        }
        val result = EffectSchema(resultedClauses)
        return result
    }
}

fun (Imply).effectsAsList() : List<Effect> = right.filter { it is Effect }.toList() as List<Effect>