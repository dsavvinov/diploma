package main.structure.schema.operators

import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.lift
import main.structure.schema.SchemaVisitor


data class Not(override val arg: EsNode) : UnaryOperator {
    override fun <T> accept(visitor: SchemaVisitor<T>): T = visitor.visit(this)
    override fun newInstance(arg: EsNode): UnaryOperator = Not(arg)

    override fun reduce(): EsNode {
        if (arg is EsConstant) {
            return (arg == false.lift()).lift()
        }

        return this
    }
}

