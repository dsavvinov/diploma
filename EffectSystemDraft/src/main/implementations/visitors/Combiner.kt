package main.implementations.visitors

import main.implementations.EffectSchemaImpl
import main.implementations.visitors.helpers.filter
import main.implementations.visitors.helpers.firstOrNull
import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsType
import main.structure.general.EsVariable
import main.structure.lift
import main.structure.schema.EffectSchema
import main.structure.schema.SchemaVisitor
import main.structure.schema.effects.Calls
import main.structure.schema.effects.Outcome
import main.structure.schema.effects.Returns
import main.structure.schema.effects.Throws
import main.structure.schema.operators.And
import main.structure.schema.operators.BinaryOperator
import main.structure.schema.operators.Imply
import main.structure.schema.operators.UnaryOperator

class Combiner : SchemaVisitor<EsNode> {
    override fun visit(schema: EffectSchema): EsNode {
        val evaluatedEffects = schema.clauses.flatMap {
            val res = it.accept(this)
            when (res) {
                is Imply -> listOf(res)
                is EffectSchema -> res.clauses
                else -> throw IllegalStateException()
            }
        }

        return EffectSchemaImpl(evaluatedEffects)
    }

    override fun visit(variable: EsVariable): EsNode = variable

    override fun visit(constant: EsConstant): EsNode = constant

    override fun visit(type: EsType): EsNode = type

    override fun visit(binaryOperator: BinaryOperator): EsNode {
        val lhs = binaryOperator.left.accept(this)
        val rhs = binaryOperator.right.accept(this)

        return binaryOperator.newInstance(lhs, rhs).flatten()
    }

    override fun visit(unaryOperator: UnaryOperator): EsNode {
        val arg = unaryOperator.arg.accept(this)

        return unaryOperator.newInstance(arg).flatten()
    }

    override fun visit(throws: Throws) = throws

    override fun visit(returns: Returns): EsNode {
        val arg = returns.value.accept(this)
        return Returns(arg, returns.type)
    }

    override fun visit(calls: Calls): EsNode = calls
}

fun (EsNode).flatten() : EsNode = Combiner().let { accept(it) }

// TODO: another example :)
fun (EsNode).getOutcome() : Outcome = firstOrNull { it is Returns || it is Throws } as Outcome


fun (Imply).removeOutcome() : Imply {
    val conclusionWithoutOutcome = right.filter { it !is Returns && it !is Throws } ?: true.lift()
    return Imply(left, conclusionWithoutOutcome)
}

fun (Imply).removeReturns(): Imply = Imply(left, right.filter { it !is Returns } ?: true.lift())

fun (EsNode).and(node: EsNode) : EsNode {
    if (this == true.lift()) return node
    if (node == true.lift()) return this
    return And(this, node)
}