package main.structure

import main.util.isSubtypeOf

data class Is(override val left: Node, override val right: Type) : BinaryOperator {
    override fun accept(visitor: Visitor): Node {
        return visitor.visit(this)
    }

    override fun isImplies(op: Operator): Boolean {
        return op is Is && left == op.left && right.isSubtypeOf(op.right)
    }
}

data class Equal(override val left: Node, override val right: Node) : BinaryOperator {
    override fun accept(visitor: Visitor): Node {
        return visitor.visit(this)
    }

    override fun isImplies(op: Operator): Boolean {
        return op is Equal && left == op.left && right == op.right
    }
}