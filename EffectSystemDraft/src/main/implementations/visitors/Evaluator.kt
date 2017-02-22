package main.implementations.visitors

import main.implementations.ClauseImpl
import main.implementations.EffectSchemaImpl
import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsType
import main.structure.general.EsVariable
import main.structure.lift
import main.structure.schema.Clause
import main.structure.schema.EffectSchema
import main.structure.schema.SchemaVisitor
import main.structure.schema.effects.Returns
import main.structure.schema.effects.Throws
import main.structure.schema.operators.BinaryOperator
import main.structure.schema.operators.UnaryOperator

class Evaluator : SchemaVisitor<EsNode> {
    override fun visit(schema: EffectSchema): EsNode {
        val effects = schema.clauses.map { it.accept(this) as Clause }.filter { it.premise != false.lift() }
        return EffectSchemaImpl(effects)
    }

    override fun visit(clause: Clause): EsNode {
        val evaluatedPremise = clause.premise.accept(this)
        val evaluatedConclusion = clause.conclusion.accept(this)

        return ClauseImpl(evaluatedPremise, evaluatedConclusion)
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