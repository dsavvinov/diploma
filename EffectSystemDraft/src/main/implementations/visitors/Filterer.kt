package main.implementations.visitors

import main.implementations.EffectImpl
import main.implementations.EffectSchemaImpl
import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsType
import main.structure.general.EsVariable
import main.structure.schema.*
import main.structure.schema.operators.Equal
import main.structure.schema.operators.Is

class Filterer(val predicate: (EsNode) -> Boolean) : SchemaVisitor<EsNode?> {
    override fun visit(schema: EffectSchema): EsNode? {
        if (!predicate(schema)) {
            return null
        }

        return EffectSchemaImpl(
                schema.function,
                schema.returnVar,
                schema.effects.map { it.accept(this) }.filterIsInstance<Effect>()
        )
    }

    override fun visit(effect: Effect): EsNode? {
        if (!predicate(effect)) {
            return null
        }

        val filteredPremise = effect.premise.accept(this) ?: return null
        val filteredConclusion = effect.conclusion.accept(this) ?: return null

        return EffectImpl(filteredPremise, filteredConclusion)
    }

    override fun visit(variable: EsVariable): EsNode? {
        if (!predicate(variable)) {
            return null
        }
        return variable
    }

    override fun visit(constant: EsConstant): EsNode? {
        if (!predicate(constant)) {
            return null
        }
        return constant
    }

    override fun visit(type: EsType): EsNode? {
        if (!predicate(type)) {
            return null
        }
        return type
    }

    override fun visit(isOp: Is): EsNode? {
        if (!predicate(isOp)) {
            return null
        }

        val filteredLhs = isOp.left.accept(this) ?: return null
        val filteredRhs = isOp.right.accept(this) as? EsType ?: return null

        return Is(filteredLhs, filteredRhs)
    }

    override fun visit(equalOp: Equal): EsNode? {
        if (!predicate(equalOp)) {
            return null
        }

        val filteredLhs = equalOp.left.accept(this) ?: return null
        val filteredRhs = equalOp.right.accept(this) ?: return null

        return Equal(filteredLhs, filteredRhs)
    }

    override fun visit(throwsOp: Throws): EsNode? {
        if (!predicate(throwsOp)) {
            return null
        }

        return throwsOp
    }

    override fun visit(orOp: Or): EsNode? {
        if (!predicate(orOp)) {
            return null
        }

        val filteredLhs = orOp.left.accept(this) ?: return null
        val filteredRhs = orOp.right.accept(this) ?: return null

        return Or(filteredLhs, filteredRhs)
    }

    override fun visit(andOp: And): EsNode? {
        if (!predicate(andOp)) {
            return null
        }

        val filteredLhs = andOp.left.accept(this) ?: return null
        val filteredRhs = andOp.right.accept(this) ?: return null

        return Equal(filteredLhs, filteredRhs)
    }

    override fun visit(notOp: Not): EsNode? {
        if (!predicate(notOp)) {
            return null
        }

        val filteredArg = notOp.arg.accept(this) ?: return null

        return Not(filteredArg)
    }

    override fun visit(returns: Returns): EsNode? {
        if (!predicate(returns)) {
            return null
        }
        return returns
    }
}

fun (EsNode).filter(predicate: (EsNode) -> Boolean): EsNode? = Filterer(predicate).let { accept(it) }
