package main.implementations.call

import main.structure.call.CtCall
import main.structure.call.CtNode
import main.structure.general.EsFunction
import main.structure.general.EsType

data class CtCallImpl(override val function: EsFunction, override val childs: List<CtNode>) : CtCall