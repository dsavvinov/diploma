package system

import system.FALSE
import system.TRUE

abstract class Expression {
    abstract fun evaluate(): Expression
    abstract fun bind(context: Map<Variable, Expression>): Expression
}

open class Variable(val name: String, val value: Value, val type: Type) : Expression() {
    override fun toString(): String {
        return name
    }

    override fun evaluate(): Expression {
        return this
    }

    override fun bind(context: Map<Variable, Expression>): Expression {
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

class Constant(value: Value, type: Type) : Variable("", value, type) {
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


}

data class Function(val name: String)

data class Application(val function: Function, var args: Map<Variable, Expression>) : Expression() {
    override fun evaluate(): Expression {
        return Context.getEffectSchema(function).bind(args).evaluate()
    }

    override fun bind(context: Map<Variable, Expression>): Application {
        val newMap: MutableMap<Variable, Expression> = mutableMapOf()
        newMap.putAll(args)

        for ((variable, expression) in context) {
            newMap[variable] = expression
        }

        args = newMap

        return this
    }
}

data class Value(val value: Any) {
    override fun toString(): String {
        return value.toString()
    }
}
data class Exception(val name: String)