package main.implementations.visitors

import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsType
import main.structure.general.EsVariable
import main.structure.schema.*
import main.structure.schema.operators.Equal
import main.structure.schema.operators.Is

class Searcher(val predicate: (EsNode) -> Boolean) : SchemaVisitor<Unit> {
    val buffer: MutableList<EsNode> = mutableListOf()

    private fun tryAdd(node: EsNode) {
        if (predicate(node)) {
            buffer.add(node)
        }
    }
    override fun visit(schema: EffectSchema) {
        schema.clauses.forEach { it.accept(this) }
        tryAdd(schema)
    }

    override fun visit(clause: Clause) {
        clause.premise.accept(this)
        clause.conclusion.accept(this)
        tryAdd(clause)
    }

    override fun visit(variable: EsVariable) = tryAdd(variable)

    override fun visit(constant: EsConstant) = tryAdd(constant)

    override fun visit(type: EsType) = tryAdd(type)

    override fun visit(isOp: Is) {
        isOp.left.accept(this)
        isOp.right.accept(this)
        tryAdd(isOp)
    }

    override fun visit(equalOp: Equal) {
        equalOp.left.accept(this)
        equalOp.right.accept(this)
        tryAdd(equalOp)
    }

    override fun visit(throwsOp: Throws): Unit = tryAdd(throwsOp)

    override fun visit(orOp: Or) {
        orOp.left.accept(this)
        orOp.right.accept(this)
        tryAdd(orOp)
    }

    override fun visit(andOp: And) {
        andOp.left.accept(this)
        andOp.right.accept(this)
        tryAdd(andOp)
    }

    override fun visit(notOp: Not) {
        notOp.arg.accept(this)
        tryAdd(notOp)
    }

    override fun visit(returns: Returns): Unit = tryAdd(returns)
}

fun (EsNode).findAll(predicate: (EsNode) -> Boolean): List<EsNode> =
        Searcher(predicate).let { accept(it); it.buffer }

fun (EsNode).firstOrNull(predicate: (EsNode) -> Boolean): EsNode? =
        findAll(predicate).firstOrNull()

fun (EsNode).contains(predicate: (EsNode) -> Boolean) =
        findAll(predicate).isNotEmpty()