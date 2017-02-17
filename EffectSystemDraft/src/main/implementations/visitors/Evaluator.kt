package main.implementations.visitors

import main.implementations.ClauseImpl
import main.implementations.EffectSchemaImpl
import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsType
import main.structure.general.EsVariable
import main.structure.lift
import main.structure.schema.*
import main.structure.schema.operators.Equal
import main.structure.schema.operators.Is

class Evaluator : SchemaVisitor<EsNode> {
    override fun visit(schema: EffectSchema): EsNode {
        val effects = schema.clauses.map { it.accept(this) as Clause }.filter { it.premise != false.lift() }
        return EffectSchemaImpl(schema.function, schema.returnVar, effects)
    }

    override fun visit(clause: Clause): EsNode {
        val evaluatedPremise = clause.premise.accept(this)
        val evaluatedConclusion = clause.conclusion.accept(this)

        return ClauseImpl(evaluatedPremise, evaluatedConclusion)
    }

    override fun visit(variable: EsVariable): EsNode = variable

    override fun visit(constant: EsConstant): EsNode = constant

    override fun visit(type: EsType): EsNode = type

    override fun visit(isOp: Is): EsNode {
        val evLhs = isOp.left.accept(this)
        val evRhs = isOp.right.accept(this) as EsType

        when(evLhs) {
            is EsConstant -> return (evLhs.type == evRhs).lift()
            is EsVariable -> return (evLhs.type == evRhs).lift()
            else -> return Is(evLhs, evRhs)
        }
    }

    override fun visit(equalOp: Equal): EsNode {
        val evLhs = equalOp.left.accept(this)
        val evRhs = equalOp.right.accept(this)

        if (evLhs is EsConstant && evRhs is EsConstant) {
            return (evLhs.value == evRhs.value).lift()
        }

        return Equal(evLhs, evRhs)
    }

    override fun visit(throwsOp: Throws): EsNode = throwsOp

    override fun visit(orOp: Or): EsNode {
        val evLhs = orOp.left.accept(this)
        val evRhs = orOp.right.accept(this)

        if (evLhs is EsConstant && evRhs is EsConstant) {
            return (evLhs.value as Boolean || evRhs.value as Boolean).lift()
        }

        return Or(evLhs, evRhs)
    }

    override fun visit(andOp: And): EsNode {
        val evLhs = andOp.left.accept(this)
        val evRhs = andOp.right.accept(this)

        if (evLhs is EsConstant && evRhs is EsConstant) {
            return (evLhs.value as Boolean && evRhs.value as Boolean).lift()
        }

        return And(evLhs, evRhs)
    }

    override fun visit(notOp: Not): EsNode {
        val evArg = notOp.arg.accept(this)

        if (evArg is EsConstant) {
            return (evArg.value as Boolean).not().lift()
        }

        return Not(evArg)
    }

    override fun visit(returns: Returns): EsNode = returns
}

fun (EsNode).evaluate() : EsNode = Evaluator().let { this.accept(it) }