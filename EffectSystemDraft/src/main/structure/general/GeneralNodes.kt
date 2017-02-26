package main.structure.general

import main.lang.KtType
import main.structure.call.CallTreeVisitor
import main.structure.call.CtNode
import main.structure.lift
import main.structure.schema.EffectSchema
import main.structure.schema.SchemaVisitor
import main.structure.schema.Term
import main.structure.schema.effects.Returns
import main.structure.schema.operators.Imply

interface EsNode {
    fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
}

data class EsVariable(val name: String, val type: EsType) : EsNode, CtNode, Term {
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
    override fun <T> accept(visitor: CallTreeVisitor<T>): T = visitor.visit(this)

    override fun castToSchema(): EffectSchema = EffectSchema(listOf(Imply(true.lift(), Returns(this, type))))
}

data class EsConstant(val type: EsType, val value: Any?) : EsNode, CtNode, Term {
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
    override fun <T> accept(visitor: CallTreeVisitor<T>): T = visitor.visit(this)

    override fun toString(): String {
        return value.toString()
    }

    override fun castToSchema(): EffectSchema = EffectSchema(listOf(Imply(true.lift(), Returns(this, type))))
}

// TODO: composition or inheritance? Depends on the real KtType, I think
// Inheritance pros:
//   - Get all kt-types functionality (like, upper-bounds, lower-bounds, lca, subtyping, etc)
data class EsType(override val name: String) : KtType(name), EsNode {
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)

    override fun toString(): String {
        return name
    }
}

data class EsFunction(val name: String, val formalArgs: List<EsVariable>, val returnType: EsType) {
    val returnVar: EsVariable = EsVariable("return_$name", returnType)
    var schema: EffectSchema? = null
}