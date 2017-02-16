package main.implementations.visitors

import main.implementations.EffectImpl
import main.implementations.EffectSchemaImpl
import main.implementations.general.EsVariableImpl
import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsType
import main.structure.general.EsVariable
import main.structure.schema.*
import main.structure.schema.operators.Equal
import main.structure.schema.operators.Is

class Transformer(val transform: (EsNode) -> EsNode) : SchemaVisitor<EsNode> {
    override fun visit(schema: EffectSchema): EsNode = transform(EffectSchemaImpl(
            schema.function,
            schema.returnVar,
            schema.effects.map { it.accept(this) as Effect }
    ))

    override fun visit(effect: Effect): EsNode = transform(EffectImpl(
            effect.premise.accept(this),
            effect.conclusion.accept(this)
    ))

    override fun visit(variable: EsVariable): EsNode = transform(variable)

    override fun visit(constant: EsConstant): EsNode = transform(constant)

    override fun visit(type: EsType): EsNode = transform(type)

    override fun visit(isOp: Is): EsNode = transform(Is(
            isOp.left.accept(this),
            isOp.right.accept(this) as EsType
    ))

    override fun visit(equalOp: Equal): EsNode = transform(Equal(
            equalOp.left.accept(this),
            equalOp.right.accept(this)
    ))

    override fun visit(throwsOp: Throws): EsNode = transform(throwsOp)

    override fun visit(orOp: Or): EsNode = transform(Or(
            orOp.left.accept(this),
            orOp.right.accept(this)
    ))

    override fun visit(andOp: And): EsNode = transform(And(
            andOp.left.accept(this),
            andOp.right.accept(this)
    ))

    override fun visit(notOp: Not): EsNode = transform(Not(
            notOp.arg.accept(this)
    ))

    override fun visit(returns: Returns): EsNode = transform(returns)
}

fun (EsNode).transform(transform: (EsNode) -> EsNode) = Transformer(transform).let { accept(it) }

fun (EsNode).transformReturn(transform: (Returns) -> EsNode) = transform { if (it is Returns) transform(it) else it }