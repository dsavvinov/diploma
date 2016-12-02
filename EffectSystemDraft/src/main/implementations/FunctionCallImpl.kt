package main.implementations

import main.api.EffectSystem
import main.structure.Context
import main.structure.Node
import main.implementations.Function
import main.structure.FunctionCall

data class FunctionCallImpl(override val function: Function, override val args: List<Node>) : FunctionCall {
    override fun visit(context: Context): Node {
        val effectSchema = EffectSystem.getEffectSchema(function)

        // In fact, we should care about scopes, var shadowing etc. etc,
        // but we ignore it because in real life compiler handles it anyway (I hope, at least)
        for ((subst, arg) in args.zip(function.arguments)) {
            context[arg] = subst
        }
        return effectSchema.evaluate(context)
    }
}