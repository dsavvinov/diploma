package main.implementations

import main.implementations.visitors.and
import main.structure.general.EsNode
import main.structure.schema.Clause
import main.structure.schema.Effect

data class ClauseImpl(override val premise: EsNode, override val conclusion: EsNode) : Clause {
    constructor(premise: EsNode, effectsList: List<Effect>) : this(
            premise,
            effectsList.reduceRight<EsNode, Effect> { effect, acc -> acc.and(effect) }
    )
}