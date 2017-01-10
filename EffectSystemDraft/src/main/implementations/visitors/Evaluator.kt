package main.implementations.visitors

import main.api.EffectSystem
import main.implementations.*
import main.implementations.operators.*
import main.structure.*
import main.lang.*
import main.lang.Function
import main.structure.system.*
import main.util.AnyType
import main.util.lift

class Evaluator(val context: MutableMap<Variable, Constant>) : Visitor {
    val substitutions = mutableMapOf<Variable, Node>()
    var inversed = false

    override fun visit(call: FunctionCall): EffectSchema {
        val effectSchema = EffectSystem.getEffectSchema(call.function)
        for ((variable, expr) in call.function.arguments.zip(call.args)) {
            substitutions[variable] = expr
        }

        return effectSchema.accept(this)
    }

    override fun visit(schema: EffectSchema): EffectSchema {
        val schemas = mutableListOf<EffectSchema>()
        for (effect in schema.effects) {
            schemas += effect.accept(this) as EffectSchema
        }

        return EffectSchemaImpl(schemas.flatMap { it.effects }, schema.returnVar)
    }

    override fun visit(effect: Effect): Node {
        val evaluatedPremise = effect.premise.accept(this)
        val evaluatedConclusion = effect.conclusion.accept(this)

        if (evaluatedPremise is EffectSchema) {
            return EffectSchemaImpl(evaluatedPcremise.effects.map { EffectImpl(it.premise, And(it.conclusion, evaluatedConclusion)) })
        }
        return EffectSchemaImpl(listOf(EffectImpl(evaluatedPremise, evaluatedConclusion)))
    }

    override fun visit(isOperator: Is): Node {
        val evaluatedLhs = isOperator.left.accept(this)
        val evaluatedRhs = isOperator.right.accept(this)

        // TODO: think about more elegant and readable implementation
        when(evaluatedLhs) {
            is EffectSchema -> {
                // collect all premises that lead to conclusion, in which `returnVar is evaluatedRhs` derivable
                val returnVar = evaluatedLhs.returnVar
                val desiredEffect = Is(returnVar, evaluatedRhs)

                val effects = evaluatedLhs.effects
                        .map { EffectImpl(it.premise, And(it.conclusion, Is(evaluatedLhs.returnVar, evaluatedRhs)))}
                return EffectSchemaImpl(effects)
            }
            else -> return Is(evaluatedLhs, evaluatedRhs)
        }
    }

    override fun visit(equalOperator: Equal): Node {
        val evaluatedLhs = equalOperator.left.accept(this)
        val evaluatedRhs = equalOperator.right.accept(this)

        // TODO: refactor this!!!
        if (evaluatedLhs is EffectSchema) {
            when (evaluatedRhs) {
                is Constant -> return EffectSchemaImpl(evaluatedRhs.equal(evaluatedLhs))
                is Variable -> return EffectSchemaImpl(evaluatedRhs.equal(evaluatedLhs))
                is EffectSchema -> return EffectSchemaImpl(evaluatedRhs.equal(evaluatedLhs))
            }
        }

        if (evaluatedRhs is EffectSchema) {
            when (evaluatedLhs) {
                is Constant -> return EffectSchemaImpl(evaluatedLhs.equal(evaluatedRhs))
                is Variable -> return EffectSchemaImpl(evaluatedLhs.equal(evaluatedRhs))
                is EffectSchema -> return EffectSchemaImpl(evaluatedLhs.equal(evaluatedRhs))
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

    // TODO: think about proper application of logic operators to effect schemas
    override fun visit(not: Not): Node {
        inversed = inversed.xor(true)
        val arg = not.arg.accept(this)

        if (arg is EffectSchema) {
            return arg
        }
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

    fun (Variable).equal(effectSchema: EffectSchema): List<Effect> {
        val desired = Equal(effectSchema.returnVar, this)
        return effectSchema.effects.filter { it.conclusion.isImplies(desired).xor(inversed) }
    }

    fun (Constant).equal(effectSchema: EffectSchema): List<Effect> {
        val desired = Equal(effectSchema.returnVar, this)
        return effectSchema.effects.filter { it.conclusion.isImplies(desired).xor(inversed) }
    }

    fun (EffectSchema).equal(effectSchema: EffectSchema): List<Effect> {
        return effects.flatMap { thisEffect ->
            val thisReturns: Equal? = thisEffect.search { it is Equal && it.left == returnVar }.firstOrNull() as Equal?
            if (thisReturns != null) {
                effectSchema.effects
                        .filter { it.conclusion.isImplies(Equal(effectSchema.returnVar, thisReturns.right)).xor(inversed) }
                        .map { EffectImpl(
                                premise = And(thisEffect.premise, it.premise),
                                conclusion = And(thisEffect.conclusion, it.conclusion)
                        ) }
            } else {
                listOf<Effect>()
            }
        }
    }
}