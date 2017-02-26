package main.visitors.helpers

import main.structure.general.EsNode
import main.structure.schema.EffectSchema
import main.structure.schema.SchemaVisitor
import main.structure.schema.effects.Returns
import main.structure.schema.operators.BinaryOperator
import main.structure.schema.operators.Imply
import main.structure.schema.operators.UnaryOperator

class Transformer(val transform: (EsNode) -> EsNode) : SchemaVisitor<EsNode> {
    override fun visit(node: EsNode): EsNode = transform(node)

    override fun visit(schema: EffectSchema): EsNode =
            EffectSchema(schema.clauses.map { it.accept(this) as Imply })
                    .let(transform)

    override fun visit(binaryOperator: BinaryOperator): EsNode =
            binaryOperator
                    .newInstance(binaryOperator.left.accept(this), binaryOperator.right.accept(this))
                    .let(transform)

    override fun visit(unaryOperator: UnaryOperator): EsNode =
            unaryOperator
                    .newInstance(unaryOperator.arg.accept(this))
                    .let(transform)

    override fun visit(returns: Returns): EsNode = Returns(returns.value.accept(this), returns.type).let(transform)
}

fun (EsNode).transform(transform: (EsNode) -> EsNode) = Transformer(transform).let { accept(it) }

fun (Imply).transformReturn(transform: (Returns) -> EsNode) : Imply =
        transform { if (it is Returns) transform(it) else it } as Imply