package main.structure

import main.util.BooleanType

class Exception : Primitive {
    override fun accept(visitor: Visitor): Exception {
        return visitor.visit(this)
    }
}

data class Type(val name: String) : Primitive {
    override fun accept(visitor: Visitor): Type {
        return visitor.visit(this)
    }
}

data class Variable(val name: String, val type: Type) : Primitive {
    override fun accept(visitor: Visitor): Variable {
        return visitor.visit(this)
    }
}

data class Function(val arguments: List<Variable>, val returnVar: Variable) : Primitive {
    override fun accept(visitor: Visitor): Function {
        return visitor.visit(this)
    }
}

sealed class Constant(open val value: Any, open val type: Type) : Primitive
class BooleanConstant(override val value: Boolean) : Constant(value, BooleanType), LogicStatement {
    override fun accept(visitor: Visitor): BooleanConstant {
        return visitor.visit(this)
    }
}
