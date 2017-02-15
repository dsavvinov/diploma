package main.implementations

import main.structure.general.EsNode
import main.structure.schema.Effect

data class EffectImpl(override val premise: EsNode, override val conclusion: EsNode) : Effect