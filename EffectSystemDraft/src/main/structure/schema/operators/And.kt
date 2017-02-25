package main.structure.schema.operators

import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.lift
import main.structure.schema.SchemaVisitor

data class And(override val left: EsNode, override val right: EsNode) : BinaryOperator {
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
    override fun newInstance(left: EsNode, right: EsNode): BinaryOperator = And(left, right)

    override fun reduce(): EsNode {
        if (left is EsConstant) {
            if (left.value == false) {
                return false.lift()
            }
            return right
        }

        if (right is EsConstant) {
            if (right.value == false) {
                return false.lift()
            }
            return left
        }

        return this
    }
}