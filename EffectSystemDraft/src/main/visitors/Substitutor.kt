package main.visitors

import main.structure.general.EsFunction
import main.structure.general.EsNode
import main.structure.general.EsVariable
import main.structure.schema.EffectSchema
import main.structure.schema.SchemaVisitor
import main.structure.schema.operators.BinaryOperator
import main.structure.schema.operators.Imply
import main.structure.schema.operators.UnaryOperator

/**
 * Visits EffectSchema-tree and substitutes every occurence
 * of a given set of variables with corresponding node.
 *
 * Generally, used when we want to bind call-arguments to formal arguments.
 */
class Substitutor(val substs: Map<EsVariable, EsNode>) : SchemaVisitor<EsNode> {
    override fun visit(node: EsNode): EsNode = node

    override fun visit(schema: EffectSchema): EsNode {
        val substitutedEffects = schema.clauses.map { it.accept(this) as Imply }
        return EffectSchema(substitutedEffects)
    }

    override fun visit(binaryOperator: BinaryOperator): EsNode =
            binaryOperator.newInstance(binaryOperator.left.accept(this), binaryOperator.right.accept(this))

    override fun visit(unaryOperator: UnaryOperator): EsNode =
            unaryOperator.newInstance(unaryOperator.arg.accept(this))

    override fun visit(variable: EsVariable): EsNode = substs[variable] ?: variable
}

fun (EffectSchema).bind(function: EsFunction, args: List<EsNode>) : EffectSchema {
    val substs = function.formalArgs.zip(args).toMap()

    val substitutor = Substitutor(substs)
    return substitutor.visit(this) as EffectSchema
}