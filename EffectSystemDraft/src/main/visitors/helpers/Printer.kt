package main.visitors.helpers

import main.structure.general.EsConstant
import main.structure.general.EsType
import main.structure.general.EsVariable
import main.structure.schema.EffectSchema
import main.structure.schema.SchemaVisitor
import main.structure.schema.effects.Returns
import main.structure.schema.effects.Throws
import main.structure.schema.operators.*

class EffectSchemaPrinter : SchemaVisitor<Unit> {
    override fun toString(): String {
        return sb.toString()
    }

    private val sb = StringBuilder()
    private var indent = ""
    private val indentStep = "  "

    private fun indent() {
        indent += indentStep
    }

    private fun outdent() {
        indent = indent.removeSuffix(indentStep)
    }

    private fun nested(body: () -> Unit): Unit {
        sb.appendln("{")
        indent()
        body()
        outdent()
        sb.append("$indent}")
    }

    private fun inBrackets(body: () -> Unit): Unit {
        sb.append("(")
        body()
        sb.append(")")
    }

    override fun visit(schema: EffectSchema) {
        nested {
            schema.clauses.forEach { it.accept(this); sb.appendln() }
        }
    }

    override fun visit(imply: Imply): Unit {
        sb.append(indent)
        imply.left.accept(this)
        sb.append(" => ")
        imply.right.accept(this)
    }

    override fun visit(isOperator: Is): Unit {
        isOperator.left.accept(this)
        sb.append(" is ")
        isOperator.right.accept(this)
    }

    override fun visit(equalOperator: Equal): Unit {
        inBrackets {
            equalOperator.left.accept(this)
            sb.append(" == ")
            equalOperator.right.accept(this)
        }
    }

    override fun visit(throws: Throws): Unit {
        sb.append("Throws ${throws.exception}")
    }

    override fun visit(or: Or): Unit {
        inBrackets { or.left.accept(this) }
        sb.append(" OR ")
        inBrackets { or.right.accept(this) }
    }

    override fun visit(and: And): Unit {
        inBrackets { and.left.accept(this) }
        sb.append(" AND ")
        inBrackets { and.right.accept(this) }
    }

    override fun visit(not: Not): Unit {
        sb.append("NOT")
        inBrackets { not.arg.accept(this) }
    }

    override fun visit(type: EsType): Unit {
        sb.append(type.name)
    }

    override fun visit(variable: EsVariable): Unit {
        sb.append(variable.name)
    }

    override fun visit(constant: EsConstant): Unit {
        sb.append("${constant.value}")
    }

    override fun visit(returns: Returns) {
        sb.append("returns ${returns.value}: ${returns.type ?: "???"}")
    }
}

fun (EffectSchema).print() : String {
    val printer = EffectSchemaPrinter()
    printer.visit(this)
    return printer.toString()
}