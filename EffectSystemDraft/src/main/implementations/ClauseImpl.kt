package main.implementations

import main.structure.general.EsNode
import main.structure.schema.Clause

data class ClauseImpl(override val premise: EsNode, override val conclusion: EsNode) : Clause