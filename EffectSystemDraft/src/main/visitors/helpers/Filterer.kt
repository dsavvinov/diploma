package main.visitors.helpers

import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsType
import main.structure.general.EsVariable
import main.structure.lift
import main.structure.schema.EffectSchema
import main.structure.schema.SchemaVisitor
import main.structure.schema.effects.Calls
import main.structure.schema.effects.Returns
import main.structure.schema.effects.Throws
import main.structure.schema.operators.BinaryOperator
import main.structure.schema.operators.Imply
import main.structure.schema.operators.UnaryOperator

class Filterer(val predicate: (EsNode) -> Boolean) : SchemaVisitor<EsNode?> {
    override fun visit(schema: EffectSchema): EsNode? {
        if (!predicate(schema)) return null

        return EffectSchema(
                schema.clauses.map { it.accept(this) }.filterIsInstance<Imply>()
        )
    }

    override fun visit(variable: EsVariable): EsNode? {
        if (!predicate(variable)) return null
        return variable
    }

    override fun visit(constant: EsConstant): EsNode? {
        if (!predicate(constant)) return null
        return constant
    }

    override fun visit(type: EsType): EsNode? {
        if (!predicate(type)) return null
        return type
    }

    override fun visit(binaryOperator: BinaryOperator): EsNode? {
        if (!predicate(binaryOperator)) return null

        val filteredLhs = binaryOperator.left.accept(this) ?: return null
        val filteredRhs = binaryOperator.right.accept(this) ?: return null

        return binaryOperator.newInstance(filteredLhs, filteredRhs)
    }

    override fun visit(unaryOperator: UnaryOperator): EsNode? {
        if (!predicate(unaryOperator)) return null

        val filteredArg = unaryOperator.arg.accept(this) ?: return null

        return unaryOperator.newInstance(filteredArg)
    }

    override fun visit(throws: Throws): EsNode? {
        if (!predicate(throws)) return null

        return throws
    }

    override fun visit(returns: Returns): EsNode? {
        if (!predicate(returns)) return null

//        val filteredArg = returns.value.accept(this) ?: return null

        return returns
    }

    override fun visit(calls: Calls): EsNode? {
        if (!predicate(calls)) return null
        return calls
    }
}

fun (EsNode).filter(predicate: (EsNode) -> Boolean): EsNode? = Filterer(predicate).let { accept(it) }

fun (Imply).removeReturns(): Imply = Imply(left, right.filter { it !is Returns } ?: true.lift())