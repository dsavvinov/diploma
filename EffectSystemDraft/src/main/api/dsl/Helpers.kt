package main.api.dsl

import main.implementations.FunctionCallImpl
import main.structure.Function
import main.structure.FunctionCall
import main.structure.Node
import main.structure.Variable


fun (Function).schema(description: (EffectSchemaBuilder).() -> Unit) =
    EffectSchemaBuilder(this).apply { description() }.build()

fun (Function).invoke(vararg args: Node): FunctionCall {
    return FunctionCallImpl(this, args.toList())
}
