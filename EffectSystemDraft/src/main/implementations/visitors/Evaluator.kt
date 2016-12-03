package main.implementations.visitors

import main.api.EffectSystem
import main.implementations.ContextMapImpl
import main.implementations.EffectImpl
import main.implementations.EffectSchemaImpl
import main.structure.*
import main.structure.Function
import main.util.lift

class Evaluator(val context: MutableMap<Variable, Constant>) : Visitor {
    val substitutions = mutableMapOf<Variable, Node>()

    override fun visit(call: FunctionCall): Node{
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
        return EffectSchemaImpl(evaluatedEffects)
    }

    override fun visit(effect: Effect): Effect {
        val evaluatedPremise = effect.premise.accept(this)
        val evaluatedConclusion = effect.conclusion.accept(this)

        return EffectImpl(evaluatedPremise, evaluatedConclusion)
    }

    override fun visit(isOperator: Is): Node {
        val evaluatedLhs = isOperator.left.accept(this)
        val evaluatedRhs = isOperator.right.accept(this)

        // TODO: think about more elegant implementation
        when(evaluatedLhs) {
            is Variable -> return (evaluatedLhs.type == evaluatedRhs).lift()
            is Constant -> return (evaluatedLhs.type == evaluatedRhs).lift()
            is EffectSchema -> {
                // collect all premises that lead to conclusion, in which `returnArg is evaluatedRhs` derivable
                val returnVar = evaluatedLhs.function.returnVar
                val desiredEffect = Is(returnVar, evaluatedRhs)

                val premises = evaluatedLhs.effects
                        .filter { it.conclusion.isImplies(desiredEffect) }
                        .map { it.premise }

                if (premises.isEmpty()) {
                    return false.lift()
                }

                // Join all premises with OR-operator (note right fold - we want first premise be at top)
                return premises.foldRight(null as LogicStatement?, { node: LogicStatement, acc: Node? ->
                    if (acc == null) return@foldRight node
                    return@foldRight Or(node, acc as LogicStatement)
                })!!
            }
            else -> throw IllegalArgumentException("Unknown type for LHS in `Is`-operator: $evaluatedLhs")
        }
    }

    override fun visit(equalOperator: Equal) {
        val evaluatedLhs = equalOperator.left.accept(this)
        val evaluatedRhs = equalOperator.right.accept(this)
    }

    override fun visit(throwsOperator: Throws) {
        TODO("not implemented")
    }

    override fun visit(or: Or) {
        TODO("not implemented")
    }

    override fun visit(and: And) {
        TODO("not implemented")
    }

    override fun visit(not: Not) {
        TODO("not implemented")
    }

    override fun visit(exception: Exception) {
        TODO("not implemented")
    }

    override fun visit(type: Type) {
        TODO("not implemented")
    }

    override fun visit(variable: Variable) {
        TODO("not implemented")
    }

    override fun visit(function: Function) {
        TODO("not implemented")
    }

    override fun visit(booleanConstant: BooleanConstant) {
        TODO("not implemented")
    }


    fun (Variable).equal(right: Variable): BooleanConstant {
        return (
                context[this]  != null &&
                context[right] != null &&
                context[this]  == context[right]
                ).lift()
    }

    fun (Variable).equal(right: Constant): BooleanConstant {
        return (context[this] != null && context[this] == right).lift()
    }

    fun (Variable).equal(right: EffectSchema): Boolean {
        return (context[])
    }
}