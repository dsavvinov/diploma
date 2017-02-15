package main.api.dsl

import main.implementations.call.CtCallImpl
import main.structure.call.CtCall
import main.structure.call.CtNode
import main.structure.general.EsFunction

operator fun (EsFunction).invoke(vararg nodes: CtNode): CtCall = CtCallImpl(this, nodes.asList())