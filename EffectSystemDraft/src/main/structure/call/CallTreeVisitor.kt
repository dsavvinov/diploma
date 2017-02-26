package main.structure.call

import main.structure.general.EsConstant
import main.structure.general.EsVariable

interface CallTreeVisitor<out T> {
    fun visit(node: CtNode): T =
            throw IllegalStateException("Error: top-level type reached but no overloads were found!")

    fun visit(call: CtCall): T = visit(call as CtNode)

    fun visit(variable: EsVariable): T = visit(variable as CtNode)

    fun visit(constant: EsConstant): T = visit(constant as CtNode)
}
