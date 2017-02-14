package main.implementations.visitors

import main.structure.call.*
import main.structure.general.EsNode
import main.structure.schema.EffectSchema

class Substitutor : CallTreeVisitor<EsNode> {
    override fun visit(node: CtNode): EsNode {
        // TODO: stub here!
        throw IllegalStateException("bad!")
    }

    override fun visit(call: CtCall): EsNode {
        val basicSchema = call.function.schema

        val substitutedArgs = call.childs.map { it.accept(this) }

        return basicSchema.bind(substitutedArgs)
    }

    override fun visit(variable: CtVariable): EsNode {
        TODO("not implemented")
    }

    override fun visit(constant: CtConstant): EsNode {
        TODO("not implemented")
    }
}

fun (EffectSchema).bind(args: List<EsNode>): EffectSchema