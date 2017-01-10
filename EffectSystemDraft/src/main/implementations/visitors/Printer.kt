package main.implementations.visitors

import main.implementations.*
import main.implementations.operators.*
import main.structure.*
import main.lang.*
import main.lang.Function
import main.structure.system.*

class Printer : SchemaVisitor {
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
        sb.appendln("}")
    }

    private fun inBrackets(body: () -> Unit): Unit {
        sb.append("(")
        body()
        sb.append(")")
    }

    override fun visit(call: FunctionCall): Node {
        sb.append("${indent}Call of ${call.function.name} with args:")
        nested {
            call.args.forEach { it.accept(this) }
        }
        return call
    }

    override fun visit(schema: EffectSchema): EffectSchema {
        sb.append("${indent}Schema:")
        nested {
            schema.effects.forEach { it.accept(this) }
        }

        return schema
    }

    override fun visit(effect: Effect): Effect {
        sb.append(indent)
        effect.premise.accept(this)
        sb.append(" => ")
        effect.conclusion.accept(this)
        sb.appendln()

        return effect
    }

    override fun visit(isOperator: Is): Node {
        isOperator.left.accept(this)
        sb.append(" is ")
        isOperator.right.accept(this)

        return isOperator
    }

    override fun visit(equalOperator: Equal): Node {
        equalOperator.left.accept(this)
        sb.append(" == ")
        equalOperator.right.accept(this)

        return equalOperator
    }

    override fun visit(throwsOperator: Throws): Throws {
        sb.append("Throws ")
        throwsOperator.arg.accept(this)

        return throwsOperator
    }

    override fun visit(or: Or): Node {
        inBrackets { or.left.accept(this) }
        sb.append(" OR ")
        inBrackets { or.right.accept(this) }

        return or
    }

    override fun visit(and: And): Node {
        inBrackets { and.left.accept(this) }
        sb.append(" AND ")
        inBrackets { and.right.accept(this) }

        return and
    }

    override fun visit(not: Not): Node {
        sb.append("NOT")
        inBrackets { not.arg.accept(this) }

        return not
    }

    override fun visit(exception: Exception): Exception {
        sb.append(exception.name)

        return exception
    }

    override fun visit(type: Type): Type {
        sb.append(type.name)

        return type
    }

    override fun visit(variable: Variable): Node {
        sb.append(variable.name)

        return variable
    }

    override fun visit(function: Function): Function {
        sb.append("Function <${function.name}>")

        return function
    }

    override fun visit(constant: Constant): Constant {
        sb.append("${constant.value}")

        return constant
    }

    override fun visit(booleanConstant: BooleanConstant): BooleanConstant {
        sb.append("${booleanConstant.value}")

        return booleanConstant
    }
}