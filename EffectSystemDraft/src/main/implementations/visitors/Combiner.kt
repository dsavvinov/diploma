package main.implementations.visitors

import main.implementations.EffectImpl
import main.implementations.EffectSchemaImpl
import main.structure.EsBoolean
import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsType
import main.structure.general.EsVariable
import main.structure.lift
import main.structure.schema.*
import main.structure.schema.operators.Equal
import main.structure.schema.operators.Is
import main.structure.schema.operators.evaluateEqual
import main.structure.schema.operators.evaluateIs

class Combiner : SchemaVisitor<EsNode> {
    override fun visit(schema: EffectSchema): EsNode {
        val evaluatedEffects = schema.effects.flatMap {
            val res = it.accept(this)
            when (res) {
                is Effect -> listOf(res)
                is EffectSchema -> res.effects
                else -> throw IllegalStateException()
            }
        }

        return EffectSchemaImpl(schema.function, schema.returnVar, evaluatedEffects)
    }

    override fun visit(effect: Effect): EsNode {
        val evalPremise = effect.premise.accept(this)
        val evalConclusion = effect.conclusion.accept(this)

        // TODO: we can flatten it further
        if (evalPremise is EffectSchema) {
            val resultEffects = mutableListOf<Effect>()
            evalPremise.effects.map(Effect::getAndRemoveOutcome)
                    .forEach { (outcome, e) ->
                        if (outcome is Returns) {
                            if (outcome.value == true.lift()) {
                                // If outcome on this codepath if true, then lhs implies its own rhs as well as conclusion of the whole clause
                                resultEffects.add(EffectImpl(e.premise, e.conclusion.and(evalConclusion)))
                            }
                            // Note that we don't add codepaths that result in false
                        } else {
                            // Otherwise we add original effect unchanged
                            resultEffects.add(EffectImpl(e.premise, e.conclusion.and(outcome)))
                        }
                    }
            return EffectSchemaImpl(evalPremise.function, evalPremise.returnVar, resultEffects)
        }
        return EffectImpl(evalPremise, evalConclusion)
    }

    override fun visit(variable: EsVariable): EsNode = variable

    override fun visit(constant: EsConstant): EsNode = constant

    override fun visit(type: EsType): EsNode = type

    override fun visit(isOp: Is): EsNode {
        val lhs = isOp.left.accept(this)
        val rhs = isOp.right.accept(this) as EsType

        // TODO: derp, so bad
        return when(lhs) {
            is EffectSchema -> lhs.evaluateIs(rhs)
            is EsVariable -> lhs.evaluateIs(rhs)
            is EsConstant -> lhs.evaluateIs(rhs)
            else -> throw IllegalStateException("Unsupported type of left operand for Is-clause: $lhs")
        }
    }

    override fun visit(equalOp: Equal): EsNode {
        val lhs = equalOp.left.accept(this)
        val rhs = equalOp.right.accept(this)

        return when (lhs) {
            is EffectSchema -> lhs.evaluateEqual(rhs)
            is EsVariable -> Equal(lhs, rhs)
            is EsConstant -> lhs.evaluateEqual(rhs)
            else -> throw IllegalStateException("Unsupported type of lhs for Equal-cluase: $lhs")
        }
    }

    override fun visit(throwsOp: Throws): EsNode = throwsOp

    override fun visit(orOp: Or): EsNode {
        val lhs = orOp.left.accept(this)
        val rhs = orOp.right.accept(this)

        if (lhs is EsConstant && rhs is EsConstant) {
            return (lhs == true.lift() || rhs == true.lift()).lift()
        } else {
            return Or(lhs, rhs)
        }
    }

    override fun visit(andOp: And): EsNode {
        val lhs = andOp.left.accept(this)
        val rhs = andOp.right.accept(this)

        if (lhs is EsConstant && rhs is EsConstant) {
            return (lhs == true.lift() && rhs == true.lift()).lift()
        } else {
            return And(lhs, rhs)
        }
    }

    override fun visit(notOp: Not): EsNode {
        val arg = notOp.arg.accept(this)

        when (arg) {
            is EsConstant -> return (arg != true.lift()).lift()
            is EffectSchema -> return arg.transformReturn { Returns((it.value == false.lift()).lift(), EsBoolean) }
            else -> return Not(arg)
        }
    }

    override fun visit(returns: Returns): EsNode = returns
}

fun (EsNode).flatten() : EsNode = Combiner().let { accept(it) }

fun (Effect).getOutcome() : EsNode = conclusion.firstOrNull { it is Returns || it is Throws }!!

fun (Effect).getAndRemoveOutcome() : Pair<EsNode, Effect> {
    var outcome : EsNode? = null
    val conclusionWithoutOutcome = conclusion.filter { if (it is Returns || it is Throws) { outcome = it; return@filter false } else return@filter true } ?: true.lift()
    return Pair(outcome!!, EffectImpl(premise, conclusionWithoutOutcome))
}

fun (Effect).removeOutcome() : Effect {
    var outcome : EsNode? = null
    val conclusionWithoutOutcome = conclusion.filter { (it is Returns || it is Throws).apply { outcome = it }.not() } ?: true.lift()
    return EffectImpl(premise, conclusionWithoutOutcome)
}

fun (EsNode).and(node: EsNode) : EsNode = And(this, node)