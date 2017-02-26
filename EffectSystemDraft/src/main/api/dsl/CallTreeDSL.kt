package main.api.dsl

import main.structure.call.CtCall
import main.structure.call.CtNode
import main.structure.general.EsFunction

operator fun (EsFunction).invoke(vararg nodes: CtNode): CtCall = CtCall(this, nodes.asList())