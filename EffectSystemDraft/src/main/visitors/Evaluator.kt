package main.visitors

import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsType
import main.structure.general.EsVariable
import main.structure.lift
import main.structure.schema.EffectSchema
import main.structure.schema.SchemaVisitor
import main.structure.schema.effects.Returns
import main.structure.schema.effects.Throws
import main.structure.schema.operators.BinaryOperator
import main.structure.schema.operators.Imply
import main.structure.schema.operators.UnaryOperator

/**
 * Tries to advance evaluation in a given EffectSchema-tree.
 */
class Evaluator : SchemaVisitor<EsNode> {
    override fun visit(schema: EffectSchema): EsNode {
        val effects = schema.clauses.map { it.accept(this) }
        val filteredEffects = effects.filterIsInstance<Imply>().filter { it.left != false.lift() }
        return EffectSchema(filteredEffects)
    }

    override fun visit(variable: EsVariable): EsNode = variable

    override fun visit(constant: EsConstant): EsNode = constant

    override fun visit(type: EsType): EsNode = type

    override fun visit(binaryOperator: BinaryOperator): EsNode {
        val evaluatedLhs = binaryOperator.left.accept(this)
        val evaluatedRhs = binaryOperator.right.accept(this)

        return binaryOperator.newInstance(evaluatedLhs, evaluatedRhs).reduce()
    }

    override fun visit(unaryOperator: UnaryOperator): EsNode {
        val evaluatedArg = unaryOperator.arg.accept(this)

        return unaryOperator.newInstance(evaluatedArg).reduce()
    }

    override fun visit(throws: Throws): EsNode = throws

    override fun visit(returns: Returns): EsNode = returns
}

fun (EsNode).evaluate() : EsNode = Evaluator().let { this.accept(it) }