package main.implementations.visitors

import main.implementations.EffectImpl
import main.implementations.EffectSchemaImpl
import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsType
import main.structure.general.EsVariable
import main.structure.schema.*
import main.structure.schema.operators.Equal
import main.structure.schema.operators.Is

// TODO: can we somehow get rid of casts?
class Substitutor(val substs: Map<EsVariable, EsNode>) : SchemaVisitor<EsNode> {

    override fun visit(schema: EffectSchema): EsNode {
        val substitutedEffects = schema.effects.map { it.accept(this) as Effect }
        return EffectSchemaImpl(schema.function, schema.returnVar, substitutedEffects)
    }

    override fun visit(effect: Effect): EsNode = EffectImpl(effect.premise.accept(this), effect.conclusion.accept(this))

    override fun visit(isOp: Is): EsNode = Is(isOp.left.accept(this), isOp.right.accept(this) as EsType)

    override fun visit(equalOp: Equal): EsNode = Equal(equalOp.left.accept(this), equalOp.right.accept(this))

    override fun visit(orOp: Or): EsNode = Or(orOp.left.accept(this), orOp.right.accept(this))

    override fun visit(andOp: And): EsNode = And(andOp.left.accept(this), andOp.right.accept(this))

    override fun visit(notOp: Not): EsNode = Not(notOp.arg.accept(this))

    override fun visit(variable: EsVariable): EsNode = substs[variable] ?: variable

    override fun visit(constant: EsConstant): EsNode = constant

    override fun visit(throwsOp: Throws): EsNode = throwsOp

    override fun visit(type: EsType): EsNode = type

    override fun visit(returns: Returns): EsNode = returns
}

fun (EffectSchema).bind(args: List<EsNode>) : EffectSchema {
    val substs = function.formalArgs.zip(args).toMap()

    val substitutor = Substitutor(substs)
    return substitutor.visit(this) as EffectSchema
}