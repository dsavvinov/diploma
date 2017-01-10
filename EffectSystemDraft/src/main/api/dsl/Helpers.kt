package main.api.dsl

import main.lang.impls.FunctionCallImpl
import main.lang.Function
import main.structure.system.FunctionCall
import main.structure.Node
import main.lang.Variable


fun (Function).schema(description: (EffectSchemaBuilder).() -> Unit) =
    EffectSchemaBuilder(this).apply { description() }.build()

fun (Function).invoke(vararg args: Node): FunctionCall {
    return FunctionCallImpl(this, args.toList())
}
