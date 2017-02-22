package main.implementations.visitors.helpers

import main.implementations.ClauseImpl
import main.implementations.EffectSchemaImpl
import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsType
import main.structure.general.EsVariable
import main.structure.schema.Clause
import main.structure.schema.EffectSchema
import main.structure.schema.Operator
import main.structure.schema.SchemaVisitor
import main.structure.schema.effects.Returns

class Transformer(val transform: (EsNode) -> EsNode) : SchemaVisitor<EsNode> {
    override fun visit(schema: EffectSchema): EsNode = transform(EffectSchemaImpl(
            schema.clauses.map { it.accept(this) as Clause }
    ))

    override fun visit(clause: Clause): EsNode = transform(ClauseImpl(
            clause.premise.accept(this),
            clause.conclusion.accept(this)
    ))

    override fun visit(variable: EsVariable): EsNode = transform(variable)

    override fun visit(constant: EsConstant): EsNode = transform(constant)

    override fun visit(type: EsType): EsNode = transform(type)

    override fun visit(operator: Operator): EsNode = transform(operator)
}

fun (EsNode).transform(transform: (EsNode) -> EsNode) = Transformer(transform).let { accept(it) }

fun (Clause).transformReturn(transform: (Returns) -> EsNode) : Clause = transform { if (it is Returns) transform(it) else it } as Clause