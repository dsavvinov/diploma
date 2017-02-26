package main.implementations.visitors

import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsType
import main.structure.general.EsVariable
import main.structure.schema.SchemaVisitor
import main.structure.schema.operators.*

data class Collector(
        val varsValues: MutableMap<EsVariable, EsConstant>,
        val varsTypes: MutableMap<EsVariable, EsType>) : SchemaVisitor<Unit>
{
    private var isInverted : Boolean = false

    fun inverted(body: () -> Unit) = {
        isInverted.xor(true)
        body()
        isInverted.xor(true)
    }

    override fun visit(node: EsNode) = Unit

    override fun visit(binaryOperator: BinaryOperator) {
        binaryOperator.left.accept(this)
        binaryOperator.right.accept(this)
    }

    override fun visit(unaryOperator: UnaryOperator) {
        unaryOperator.arg.accept(this)
    }

    override fun visit(isOperator: Is) {
        if (isOperator.left is EsVariable) {
            varsTypes[isOperator.left] = isOperator.right
        }
    }

    override fun visit(equalOperator: Equal) {
        if (equalOperator.left is EsVariable && equalOperator.right is EsConstant) {
            varsValues[equalOperator.left] = equalOperator.right
            varsTypes[equalOperator.left] = equalOperator.right.type
            return
        }

        equalOperator.left.accept(this)
        equalOperator.right.accept(this)
    }

    override fun visit(not: Not) {
        inverted { not.arg.accept(this) }
    }
}

fun (EsNode).collect(varsValues: MutableMap<EsVariable, EsConstant>,
                     varsTypes: MutableMap<EsVariable, EsType>) =
        Collector(varsValues, varsTypes).let { this.accept(it) }
