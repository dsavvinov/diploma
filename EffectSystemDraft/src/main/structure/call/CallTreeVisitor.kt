package main.structure.call

import main.structure.general.EsConstant
import main.structure.general.EsVariable

interface CallTreeVisitor<out T> {
    fun visit(node: CtNode): T

    fun visit(call: CtCall): T

    fun visit(variable: EsVariable): T

    fun visit(constant: EsConstant): T
}
