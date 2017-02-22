package main.structure.schema.operators

import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsType
import main.structure.general.EsVariable
import main.structure.lift
import main.structure.schema.SchemaVisitor

data class Is(override val left: EsNode, override val right: EsType) : BinaryOperator {
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
    override fun newInstance(left: EsNode, right: EsNode): BinaryOperator = Is(left, right as EsType)

    override fun reduce(): EsNode {
        if (left is EsConstant) {
            return (left.type == right).lift()
        }

        // TODO: think!
        if (left is EsVariable) {
            return (left.type == right).lift()
        }

        return this
    }
}