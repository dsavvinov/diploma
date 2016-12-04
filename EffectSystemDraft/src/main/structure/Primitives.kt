package main.structure

import main.util.BooleanType

data class Exception(val name: String) : Primitive {
    override fun accept(visitor: Visitor): Exception {
        return visitor.visit(this)
    }

    override fun isImplies(op: Operator): Boolean {
        return false
    }
}

data class Type(val name: String) : Primitive {
    override fun accept(visitor: Visitor): Type {
        return visitor.visit(this)
    }

    override fun isImplies(op: Operator): Boolean {
        return false
    }
}

data class Variable(val name: String, val type: Type) : Primitive {
    override fun accept(visitor: Visitor): Node {
        return visitor.visit(this)
    }

    override fun isImplies(op: Operator): Boolean {
        return false
    }
}

data class Function(val name: String, val arguments: List<Variable>, val returnType: Type) : Primitive {
    override fun accept(visitor: Visitor): Function {
        return visitor.visit(this)
    }

    val returnVar: Variable = Variable("return_$name", returnType)

    override fun isImplies(op: Operator): Boolean {
        return false
    }
}

open class Constant(open val value: Any, val type: Type) : Primitive {
    override fun accept(visitor: Visitor): Node {
        return visitor.visit(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Constant

        if (value != other.value) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

    override fun isImplies(op: Operator): Boolean {
        return false
    }
}

data class BooleanConstant(override val value: Boolean) : Constant(value, BooleanType) {
    override fun accept(visitor: Visitor): BooleanConstant {
        return visitor.visit(this)
    }

    override fun isImplies(op: Operator): Boolean {
        return false
    }
}
