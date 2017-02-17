package main.structure.schema

import main.structure.general.*


interface EffectSchema : EsNode {
    val returnVar: EsVariable
    val clauses: List<Clause>
    val function: EsFunction
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
}

interface Clause : EsNode {
    val premise: EsNode
    val conclusion: EsNode
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
}

data class Throws(val exception: Any?) : EsNode {
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
}

data class Or(val left: EsNode, val right: EsNode) : EsNode {
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
}

data class And(val left: EsNode, val right: EsNode) : EsNode {
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
}

data class Not(val arg: EsNode) : EsNode {
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
}

data class Returns(val value: EsConstant?, val type: EsType?) : EsNode {
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
}