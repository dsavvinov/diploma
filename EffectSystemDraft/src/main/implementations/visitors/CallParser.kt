package main.implementations.visitors

import main.lang.*
import main.structure.*

class CallParser(val context: Context) : CallTreeVisitor<EffectSchema> {
    override fun visit(call: FunctionCall): EffectSchema {
        val functionSchema = context.getFunctionSchema(call.function)
        val params = call.function.parameters
        val evaluatedValues: List<Node> = call.args.map { it.accept(this) }

        val substitutor = Substitutor()
    }

    override fun visit(isOperator: IsOperator): EffectSchema {
        TODO("not implemented")
    }

    override fun visit(equalOperator: EqualOperator): EffectSchema {
        TODO("not implemented")
    }

    override fun visit(variable: KtVariable): EffectSchema {
        TODO("not implemented")
    }
}