package main.structure.schema.operators

import main.structure.EsBoolean
import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsType
import main.structure.lift
import main.structure.schema.EffectSchema
import main.structure.schema.SchemaVisitor
import main.structure.schema.Term
import main.structure.schema.effects.Returns
import main.visitors.helpers.transform

data class Is(override val left: EsNode, override val right: EsType) : BinaryOperator {
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
    override fun newInstance(left: EsNode, right: EsNode): BinaryOperator = Is(left, right as EsType)

    override fun reduce(): EsNode {
        if (left is EsConstant) {
            return (left.type == right).lift()
        }

        return this
    }

    override fun flatten(): EsNode {
        if (left !is Term) return this

        val leftSchema: EffectSchema = left.castToSchema()

        val combinedClauses = mutableListOf<Imply>()

        for ((lhs, rhs) in leftSchema.clauses) {
            val rewritedRhs = rhs.transform {
                if (it !is Returns) {
                    return@transform it
                }

                // Otherwise evaluate Is-operator and update Returns-clause accordingly
                return@transform Returns(Is(it.value, right), EsBoolean)
            }

            combinedClauses += Imply(lhs, rewritedRhs)
        }

        return EffectSchema(combinedClauses)
    }
}