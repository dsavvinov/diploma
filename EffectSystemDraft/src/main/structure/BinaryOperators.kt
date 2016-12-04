package main.structure

import main.util.isSubtypeOf

class Is(override val left: Node, override val right: Type) : BinaryOperator, LogicStatement {
    override fun accept(visitor: Visitor): LogicStatement {
        return visitor.visit(this)
    }

    override fun isImplies(stmt: LogicStatement): Boolean {
        return stmt is Is && left == stmt.left && right.isSubtypeOf(stmt.right)
    }
}

class Equal(override val left: Node, override val right: Node) : BinaryOperator, LogicStatement {
    override fun accept(visitor: Visitor): LogicStatement {
        return visitor.visit(this)
    }

    override fun isImplies(stmt: LogicStatement): Boolean {
        return stmt is Equal && left == stmt.left && left == stmt.right
    }
}