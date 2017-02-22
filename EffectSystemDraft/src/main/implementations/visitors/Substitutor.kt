package main.implementations.visitors

import main.implementations.ClauseImpl
import main.implementations.EffectSchemaImpl
import main.structure.general.EsConstant
import main.structure.general.EsFunction
import main.structure.general.EsNode
import main.structure.general.EsVariable
import main.structure.schema.Clause
import main.structure.schema.Effect
import main.structure.schema.EffectSchema
import main.structure.schema.SchemaVisitor
import main.structure.schema.operators.BinaryOperator
import main.structure.schema.operators.Equal
import main.structure.schema.operators.UnaryOperator

// TODO: can we somehow get rid of casts?
class Substitutor(val substs: Map<EsVariable, EsNode>) : SchemaVisitor<EsNode> {

    override fun visit(schema: EffectSchema): EsNode {
        val substitutedEffects = schema.clauses.map { it.accept(this) as Clause }
        return EffectSchemaImpl(substitutedEffects)
    }

    override fun visit(clause: Clause): EsNode = ClauseImpl(clause.premise.accept(this), clause.conclusion.accept(this))

    override fun visit(binaryOperator: BinaryOperator): EsNode =
            binaryOperator.newInstance(binaryOperator.left.accept(this), binaryOperator.right.accept(this))

    override fun visit(equalOperator: Equal): EsNode {
        println("hit!")
        return super.visit(equalOperator)
    }

    override fun visit(unaryOperator: UnaryOperator): EsNode =
            unaryOperator.newInstance(unaryOperator.arg.accept(this))

    override fun visit(variable: EsVariable): EsNode = substs[variable] ?: variable

    override fun visit(constant: EsConstant): EsNode = constant

    override fun visit(effect: Effect): EsNode = effect
}

fun (EffectSchema).bind(function: EsFunction, args: List<EsNode>) : EffectSchema {
    val substs = function.formalArgs.zip(args).toMap()

    val substitutor = Substitutor(substs)
    return substitutor.visit(this) as EffectSchema
}