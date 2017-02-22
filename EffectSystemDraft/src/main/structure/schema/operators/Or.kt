package main.structure.schema.operators

import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.lift
import main.structure.schema.SchemaVisitor

data class Or(override val left: EsNode, override val right: EsNode) : BinaryOperator {
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
    override fun newInstance(left: EsNode, right: EsNode): BinaryOperator = Or(left, right)

    override fun reduce(): EsNode {
        if (left is EsConstant && right is EsConstant) {
            return (left.value == right.value).lift()
        }

        return this
    }
}

