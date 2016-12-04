package main.implementations.visitors

import main.api.EffectSystem
import main.implementations.EffectImpl
import main.implementations.EffectSchemaImpl
import main.structure.*
import main.structure.Function
import main.util.foldWith
import main.util.lift

class Evaluator(val context: MutableMap<Variable, Constant>) : Visitor {
    val substitutions = mutableMapOf<Variable, Node>()

    override fun visit(call: FunctionCall): EffectSchema {
        val effectSchema = EffectSystem.getEffectSchema(call.function)
        for ((variable, expr) in call.function.arguments.zip(call.args)) {
            substitutions[variable] = expr
        }

        return effectSchema.accept(this)
    }

    override fun visit(schema: EffectSchema): EffectSchema {
        val evaluatedEffects = mutableListOf<Effect>()
        for (effect in schema.effects) {
            evaluatedEffects += effect.accept(this)
        }
        return EffectSchemaImpl(schema.function, evaluatedEffects)
    }

    override fun visit(effect: Effect): Effect {
        val evaluatedPremise = effect.premise.accept(this)
        val evaluatedConclusion = effect.conclusion.accept(this)

        return EffectImpl(evaluatedPremise, evaluatedConclusion)
    }

    override fun visit(isOperator: Is): LogicStatement {
        val evaluatedLhs = isOperator.left.accept(this)
        val evaluatedRhs = isOperator.right.accept(this)

        // TODO: think about more elegant and readable implementation
        when(evaluatedLhs) {
            is EffectSchema -> {
                // collect all premises that lead to conclusion, in which `returnVar is evaluatedRhs` derivable
                val returnVar = evaluatedLhs.function.returnVar
                val desiredEffect = Is(returnVar, evaluatedRhs)

                val premises = evaluatedLhs.effects
                        .filter { it.conclusion.isImplies(desiredEffect) }
                        .map { it.premise }

                if (premises.isEmpty()) {
                    return false.lift()
                }

                // Join all premises with OR-operator
                return premises.foldWith(::Or)
            }
            else -> return Is(evaluatedLhs, evaluatedRhs)
        }
    }

    override fun visit(equalOperator: Equal): LogicStatement {
        val evaluatedLhs = equalOperator.left.accept(this)
        val evaluatedRhs = equalOperator.right.accept(this)

        // TODO: refactor this!!!
        if (evaluatedLhs is EffectSchema) {
            when (evaluatedRhs) {
                is Constant -> return evaluatedRhs.equal(evaluatedLhs).foldWith(::Or)
                is Variable -> return evaluatedRhs.equal(evaluatedLhs).foldWith(::Or)
                is EffectSchema -> return evaluatedRhs.equal(evaluatedLhs).foldWith(::Or)
            }
        }

        if (evaluatedRhs is EffectSchema) {
            when (evaluatedLhs) {
                is Constant -> return evaluatedLhs.equal(evaluatedRhs).foldWith(::Or)
                is Variable -> return evaluatedLhs.equal(evaluatedRhs).foldWith(::Or)
                is EffectSchema -> return evaluatedLhs.equal(evaluatedRhs).foldWith(::Or)
            }
        }

        return Equal(evaluatedLhs, evaluatedRhs)
    }

    override fun visit(throwsOperator: Throws): Throws {
        return throwsOperator
    }

    override fun visit(or: Or): Or {
        val left = or.left.accept(this)
        val right = or.right.accept(this)

        return Or(left, right)
    }

    override fun visit(and: And): And {
        val left =  and.left.accept(this)
        val right = and.right.accept(this)

        return And(left, right)
    }

    override fun visit(not: Not): Not {
        val arg = not.arg.accept(this)
        return Not(arg)
    }

    override fun visit(variable: Variable): Node {
        val node = substitutions[variable]
        node ?: return variable
        return node.accept(this)
    }

    override fun visit(booleanConstant: BooleanConstant): BooleanConstant {
        return booleanConstant
    }

    override fun visit(exception: Exception): Exception {
        return exception
    }

    override fun visit(type: Type): Type {
        return type
    }

    override fun visit(function: Function): Function {
        return function
    }

    override fun visit(constant: Constant): Constant {
        return constant
    }

    fun (Variable).equal(effectSchema: EffectSchema): List<LogicStatement> {
        return effectSchema.effects.map {
            And(it.premise, Equal(effectSchema.function.returnVar, this))
        }
    }

    fun (Constant).equal(effectSchema: EffectSchema): List<LogicStatement> {
        return effectSchema.effects.map {
                And(it.premise, Equal(effectSchema.function.returnVar, this))
        }
    }

    fun (EffectSchema).equal(effectSchema: EffectSchema): List<LogicStatement> {
        return effects.flatMap { thisEffect ->
            effectSchema.effects.map { otherEffect ->
                    listOf(
                        thisEffect.premise,
                        otherEffect.premise,
                        Equal(function.returnVar, effectSchema.function.returnVar)
                    ).foldWith(::And)
            }
        }
    }
}