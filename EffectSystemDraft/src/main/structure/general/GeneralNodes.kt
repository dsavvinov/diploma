package main.structure.general

import main.implementations.general.EsVariableImpl
import main.lang.KtType
import main.structure.call.CallTreeVisitor
import main.structure.call.CtNode
import main.structure.schema.EffectSchema
import main.structure.schema.SchemaVisitor

interface EsNode {
    fun <T> accept(visitor: SchemaVisitor<T>): T
}

interface EsVariable : EsNode, CtNode {
    val name: String
    val type: EsType

    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
    override fun <T> accept(visitor: CallTreeVisitor<T>): T = visitor.visit(this)
}

interface EsConstant : EsNode, CtNode {
    val type: EsType
    val value: Any?

    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
    override fun <T> accept(visitor: CallTreeVisitor<T>): T = visitor.visit(this)
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

// TODO: think about identity of functions
//  On the one hand, sometimes (e.g. for working with declaration) we don't want to distingiush different calls
//  On the other hand, sometimes we have to distinguish different calls (for example, to not mess together different
//      return vars).
data class EsFunction(val name: String, val formalArgs: List<EsVariable>, val returnType: EsType) {
    val returnVar: EsVariable = EsVariableImpl("return_$name", returnType)
    var schema: EffectSchema? = null
}