package main.implementations.visitors.helpers

import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsType
import main.structure.general.EsVariable
import main.structure.schema.EffectSchema
import main.structure.schema.SchemaVisitor
import main.structure.schema.effects.Returns
import main.structure.schema.operators.BinaryOperator
import main.structure.schema.operators.UnaryOperator

class Searcher(val predicate: (EsNode) -> Boolean) : SchemaVisitor<Unit> {
    val buffer: MutableList<EsNode> = mutableListOf()

    private fun tryAdd(node: EsNode) {
        if (predicate(node)) {
            buffer.add(node)
        }
    }

    override fun visit(node: EsNode) = tryAdd(node)

    override fun visit(schema: EffectSchema) {
        schema.clauses.forEach { it.accept(this) }
        tryAdd(schema)
    }

    override fun visit(variable: EsVariable) = tryAdd(variable)

    override fun visit(constant: EsConstant) = tryAdd(constant)

    override fun visit(type: EsType) = tryAdd(type)

    override fun visit(returns: Returns) {
        returns.value.accept(this)
        tryAdd(returns)
    }

    override fun visit(binaryOperator: BinaryOperator) {
        binaryOperator.left.accept(this)
        binaryOperator.right.accept(this)
        tryAdd(binaryOperator)
    }

    override fun visit(unaryOperator: UnaryOperator) {
        unaryOperator.arg.accept(this)
        tryAdd(unaryOperator.arg)
    }
}

fun (EsNode).findAll(predicate: (EsNode) -> Boolean): List<EsNode> =
        Searcher(predicate).let { accept(it); it.buffer }

fun (EsNode).firstOrNull(predicate: (EsNode) -> Boolean): EsNode? =
        findAll(predicate).firstOrNull()

fun (EsNode).contains(predicate: (EsNode) -> Boolean) =
        findAll(predicate).isNotEmpty()