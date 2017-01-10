package main.implementations.visitors

import main.implementations.EffectImpl
import main.implementations.EffectSchemaImpl
import main.structure.*

class Substitutor(val effectSchema: EffectSchema, val substs: Map<Variable, Node>) : SchemaVisitor<Node> {
    override fun visit(schema: EffectSchema): EffectSchema {
        val substitutedEffects: List<Effect> = schema.effects.map { it.accept(this) as Effect }
        return EffectSchemaImpl(substitutedEffects, effectSchema.returnVar)
    }

    override fun visit(effect: Effect): Effect {
        val substitutedPremise = effect.premise.accept(this)
        val substitutedConclusion = effect.conclusion.accept(this)

        return EffectImpl(substitutedPremise, substitutedConclusion)
    }

    override fun visit(operator: Operator): Operator {
        TODO("not implemented")
    }

    override fun visit(variable: Variable): Node {
        TODO("not implemented")
    }
}