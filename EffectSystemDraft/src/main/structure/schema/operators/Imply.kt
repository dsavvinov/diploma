package main.structure.schema.operators

import main.implementations.EffectSchemaImpl
import main.implementations.visitors.and
import main.implementations.visitors.getOutcome
import main.implementations.visitors.helpers.filter
import main.implementations.visitors.helpers.print
import main.implementations.visitors.helpers.toList
import main.implementations.visitors.removeOutcome
import main.implementations.visitors.removeReturns
import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsVariable
import main.structure.lift
import main.structure.schema.Effect
import main.structure.schema.EffectSchema
import main.structure.schema.SchemaVisitor
import main.structure.schema.effects.EffectsPipelineFlags
import main.structure.schema.effects.Returns
import main.structure.schema.effects.Throws
import main.structure.schema.effects.merge

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

    override fun reduce(): EsNode {
//        if (left == true.lift()) {
//            return right
//        }
        return this
    }

    private fun (EffectSchema).flattenImply(right: EsNode): EffectSchema {
        return when (right) {
            is EffectSchema -> flattenImply(right)
            else -> flattenImply(EffectSchemaImpl(listOf(Imply(true.lift(), right))))
        }
    }

    private fun (EffectSchema).flattenImply(right: EffectSchema): EffectSchema {
//        println("Flattening:")
//        println(this.print())
//        println("and")
//        println(right.print())

        val resultedClauses = mutableListOf<Imply>()
        for (leftClause in clauses) {
            val outcome = leftClause.getOutcome()

            if (outcome !is Returns) {
                // Add non-successfull clause as is
                resultedClauses.add(leftClause as Imply)
                continue
            }

            val lClauseWithoutReturns = (leftClause as Imply).removeReturns()

            // Now, as we know that left part is finishing and returns something,
            // we can add additional condition which will be true iff leftClause returned true
            val cond = Equal(outcome.value, true.lift())

            for (rightClause in right.clauses) {
                // Form the premise for combined clause: leftClause.left && rightClause.left && cond
                val premise = leftClause.left.and((rightClause as Imply).left).and(cond)

                // Form the conclusion for combined clause:
                //      leftClause.right.removeOutcome() && rightClause.right
                val conclusion = lClauseWithoutReturns.right.and(rightClause.right)
                resultedClauses.add(Imply(premise, conclusion))
            }
        }
        val result = EffectSchemaImpl(resultedClauses)
//        println("result:")
//        println(result.print())
//        println("================")
//        println()
        return result
    }
}

fun (Imply).effectsAsList() : List<Effect> = right.filter { it is Effect }.toList() as List<Effect>