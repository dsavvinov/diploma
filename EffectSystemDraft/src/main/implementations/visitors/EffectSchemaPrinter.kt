package main.implementations.visitors

import main.structure.general.EsConstant
import main.structure.general.EsFunction
import main.structure.general.EsType
import main.structure.general.EsVariable
import main.structure.schema.*
import main.structure.schema.operators.Equal
import main.structure.schema.operators.Is

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
        sb.append("Schema of ${schema.function.name}: ")
        nested {
            schema.clauses.forEach { it.accept(this) }
        }
    }

    override fun visit(clause: Clause): Unit {
        sb.append(indent)
        clause.premise.accept(this)
        sb.append(" => ")
        clause.conclusion.accept(this)
        sb.appendln()
    }

    override fun visit(isOp: Is): Unit {
        isOp.left.accept(this)
        sb.append(" is ")
        isOp.right.accept(this)
    }

    override fun visit(equalOp: Equal): Unit {
        equalOp.left.accept(this)
        sb.append(" == ")
        equalOp.right.accept(this)
    }

    override fun visit(throwsOp: Throws): Unit{
        sb.append("Throws ${throwsOp.exception}")
    }

    override fun visit(orOp: Or): Unit {
        inBrackets { orOp.left.accept(this) }
        sb.append(" OR ")
        inBrackets { orOp.right.accept(this) }
    }

    override fun visit(andOp: And): Unit {
        inBrackets { andOp.left.accept(this) }
        sb.append(" AND ")
        inBrackets { andOp.right.accept(this) }
    }

    override fun visit(notOp: Not): Unit {
        sb.append("NOT")
        inBrackets { notOp.arg.accept(this) }
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
        sb.append("returns ${returns.value ?: "???"}: ${returns.type ?: "???"}")
    }
}

fun (EffectSchema).print() : String {
    val printer = EffectSchemaPrinter()
    printer.visit(this)
    return printer.toString()
}