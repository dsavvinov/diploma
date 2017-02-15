package main.implementations.visitors

import main.structure.call.*
import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsVariable
import main.structure.schema.EffectSchema

class EffectSchemaGenerator : CallTreeVisitor<EsNode> {
    override fun visit(node: CtNode): EsNode {
        // TODO: stub here!
        throw IllegalStateException("bad!")
    }

    override fun visit(call: CtCall): EsNode {
        val basicSchema = call.function.schema

        val substitutedArgs = call.childs.map { it.accept(this) }

        return basicSchema!!.bind(substitutedArgs)
    }

    override fun visit(variable: EsVariable): EsNode = variable

    override fun visit(constant: EsConstant): EsNode = constant
}

fun (CtCall).generateEffectSchema() : EffectSchema {
    val generator = EffectSchemaGenerator()

    return accept(generator) as EffectSchema
}