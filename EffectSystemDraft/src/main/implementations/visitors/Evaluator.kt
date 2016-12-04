package main.implementations.visitors

import main.api.EffectSystem
import main.implementations.EffectImpl
import main.implementations.EffectSchemaImpl
import main.structure.*
import main.structure.Function
import main.util.AnyType
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
            return EffectSchemaImpl(evaluatedPremise.effects.map { EffectImpl(it.premise, And(it.conclusion, evaluatedConclusion)) })
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

    fun (Variable).equal(effectSchema: EffectSchema): List<Effect> {
        val desired = Equal(effectSchema.returnVar, this)
        return effectSchema.effects.filter { it.conclusion.isImplies(desired) }
    }

    fun (Constant).equal(effectSchema: EffectSchema): List<Effect> {
        val desired = Equal(effectSchema.returnVar, this)
        return effectSchema.effects.filter { it.conclusion.isImplies(desired) }
    }

    fun (EffectSchema).equal(effectSchema: EffectSchema): List<Effect> {
        TODO()
    }
}