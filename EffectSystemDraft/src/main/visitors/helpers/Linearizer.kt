package main.visitors.helpers

import main.structure.general.EsNode
import main.structure.schema.EffectSchema
import main.structure.schema.SchemaVisitor
import main.structure.schema.effects.Returns
import main.structure.schema.operators.BinaryOperator
import main.structure.schema.operators.UnaryOperator

class Linearizer : SchemaVisitor<List<EsNode>> {
    override fun visit(node: EsNode): List<EsNode> = listOf(node)

    override fun visit(schema: EffectSchema): List<EsNode>
            = schema.clauses.flatMap { it.accept(this) } + listOf(schema)

    override fun visit(binaryOperator: BinaryOperator): List<EsNode>
            = binaryOperator.left.accept(this) + binaryOperator.right.accept(this) + listOf(binaryOperator)

    override fun visit(unaryOperator: UnaryOperator): List<EsNode> =
            unaryOperator.arg.accept(this) + listOf(unaryOperator)
}

fun (EsNode?).toList() : List<EsNode> = Linearizer().let { this?.accept(it) } ?: listOf()