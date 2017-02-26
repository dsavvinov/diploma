package main.facade

import main.structure.call.CtCall
import main.structure.general.EsConstant
import main.structure.general.EsNode
import main.structure.general.EsType
import main.structure.general.EsVariable
import main.structure.schema.EffectSchema
import main.structure.schema.effects.Outcome
import main.visitors.collect
import main.visitors.evaluate
import main.visitors.flatten
import main.visitors.generateEffectSchema
import main.visitors.helpers.getOutcome

/**
 * Entry-point of EffectSystem. Provides simple interface for compiler.
 */
object EffectSystem {
    fun getInfo(call: CtCall, outcome: Outcome): EsInfoHolder? {
        val basicEffectSchema = call.generateEffectSchema()
        val flatEs = basicEffectSchema.flatten()
        val evaluatedEs = flatEs.evaluate() as EffectSchema

        return evaluatedEs.collectAt(outcome)
    }

    private fun (EffectSchema).collectAt(outcome: Outcome) : EsInfoHolder? {
        val varValues = mutableMapOf<EsVariable, EsConstant>()
        val varTypes = mutableMapOf<EsVariable, EsType>()

        var feasible = false
        for (clause in clauses) {
            val clauseOutcome = clause.getOutcome()
            if (outcome.followsFrom(clauseOutcome)) {
                feasible = true
                clause.left.collect(varValues, varTypes)
            }
        }

        // If at least one feasible clause was found, then return collected info
        // Otherwise, return null to indicate that requested Outcome is infeasible
        return if (feasible) EsInfoHolderImpl(varValues, varTypes) else null
    }
}

object Unknown : EsNode