package main.system

import main.system.Premise.*
import main.system.Effect.*

interface Expression {
    fun evaluate(context: Map<Variable, EffectSchema>): Expression
}

class Variable(val name: String, val value: Value, val type: Type)
    : EffectSchema(
        assertions = listOf(Const(TRUE) to Returns(value)),
        bindVars = mapOf(),
        args = setOf(),
        returnVar = null
) {
    override fun toString(): String {
        return name
    }

    override fun evaluate(context: Map<Variable, EffectSchema>): EffectSchema {
        return context[this] ?: this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Variable

        if (name != other.name) return false
        if (value != other.value) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }


}

class Constant(val value: Value, val type: Type)
    : EffectSchema(
        assertions = listOf(Const(TRUE) to Returns(value)),
        bindVars = mapOf<Variable, EffectSchema>(),
        args = setOf<Variable>(),
        returnVar = null
) {
    override fun evaluate(context: Map<Variable, EffectSchema>): Constant {
        return this
    }

    override fun toString(): String {
        return value.value.toString()
    }

    fun negate(): Constant {
        if (this == TRUE) {
            return FALSE
        }

        if (this == FALSE) {
            return TRUE
        }

        throw IllegalArgumentException("Can't negate non-boolean constant $this")
    }
}

class Type(val name: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Type

        if (name != other.name) return false

        // Insert subtyping, etc. here
        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return name
    }

    fun isSubtypeOf(other: Type): Boolean {
        return this == other
    }
}

data class Function(val name: String)

data class Application(val function: Function, var args: Map<Variable, EffectSchema>) {
    fun evaluate(): EffectSchema {
        return Context.getEffectSchema(function).evaluate(args)
    }
}

data class Value(val value: Any) {
    override fun toString(): String {
        return value.toString()
    }
}
data class Exception(val name: String)