package main.structure.schema.operators

import main.implementations.EffectSchemaImpl
import main.implementations.visitors.helpers.transform
import main.structure.EsBoolean
import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsType
import main.structure.general.EsVariable
import main.structure.lift
import main.structure.schema.EffectSchema
import main.structure.schema.SchemaVisitor
import main.structure.schema.effects.Returns

data class Is(override val left: EsNode, override val right: EsType) : BinaryOperator {
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
    override fun newInstance(left: EsNode, right: EsNode): BinaryOperator = Is(left, right as EsType)

    override fun reduce(): EsNode {
        if (left is EsConstant) {
            return (left.type == right).lift()
        }

        // TODO: think!
        if (left is EsVariable) {
            return (left.type == right).lift()
        }

        return this
    }

    override fun flatten(): EsNode {
        val leftSchema: EffectSchema = left.castToSchema()

        val combinedClauses = mutableListOf<Imply>()

        // TODO: this is not flattening! Rewrite
        for (clause in leftSchema.clauses) {
            val rewritedRhs = (clause as Imply).right.transform {
                if (it !is Returns) {
                    return@transform it
                }

                // Otherwise evalaute Is-operator and update Returns-clause accordingly
                return@transform Returns((it.type == right).lift(), EsBoolean)
            }

            combinedClauses += Imply(clause.left, rewritedRhs)
        }

        return EffectSchemaImpl(combinedClauses)
    }
}