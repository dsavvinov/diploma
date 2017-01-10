package main.implementations.visitors

import main.implementations.*
import main.implementations.operators.*
import main.structure.*
import main.lang.*
import main.lang.Function
import main.structure.system.*

class Searcher(val predicate: (Node) -> Boolean) : SchemaVisitor {
    val result: MutableList<Node> = mutableListOf()

    override fun visit(call: FunctionCall): Node {
        return call
    }

    override fun visit(schema: EffectSchema): EffectSchema {
        if (predicate(schema)) {
            result += schema
        }

        schema.effects.forEach { it.accept(this) }
        return schema
    }

    override fun visit(effect: Effect): Node {
        if (predicate(effect)) {
            result += effect
        }

        effect.premise.accept(this)
        effect.conclusion.accept(this)
        return effect
    }

    override fun visit(isOperator: Is): Node {
        if (predicate(isOperator)) {
            result += isOperator
        }

        isOperator.left.accept(this)
        isOperator.right.accept(this)

        return isOperator
    }

    override fun visit(equalOperator: Equal): Node {
        if (predicate(equalOperator)) {
            result += equalOperator
        }

        equalOperator.left.accept(this)
        equalOperator.right.accept(this)

        return equalOperator
    }

    override fun visit(throwsOperator: Throws): Throws {
        if (predicate(throwsOperator)) {
            result += throwsOperator
        }

        throwsOperator.arg.accept(this)

        return throwsOperator
    }

    override fun visit(or: Or): Node {
        if (predicate(or)) {
            result += or
        }

        or.left.accept(this)
        or.right.accept(this)

        return or
    }

    override fun visit(and: And): Node {
        if (predicate(and)) {
            result += and
        }

        and.left.accept(this)
        and.right.accept(this)

        return and
    }

    override fun visit(not: Not): Node {
        if (predicate(not)) {
            result += not
        }

        not.arg.accept(this)

        return not
    }

    override fun visit(exception: Exception): Exception {
        if (predicate(exception)) {
            result += exception
        }

        return exception
    }

    override fun visit(type: Type): Type {
        if (predicate(type)) {
            result += type
        }

        return type
    }

    override fun visit(variable: Variable): Node {
        if (predicate(variable)) {
            result += variable
        }

        return variable
    }

    override fun visit(function: Function): Function {
        if (predicate(function)) {
            result += function
        }

        return function
    }

    override fun visit(constant: Constant): Constant {
        if (predicate(constant)) {
            result += constant
        }

        return constant
    }

    override fun visit(booleanConstant: BooleanConstant): BooleanConstant {
        if (predicate(booleanConstant)) {
            result += booleanConstant
        }

        return booleanConstant
    }
}

fun (Node).search(predicate: (Node) -> Boolean): List<Node> {
    return Searcher(predicate).apply { this@search.accept(this) }.result
}