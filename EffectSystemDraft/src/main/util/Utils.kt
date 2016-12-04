package main.util

import main.structure.BooleanConstant
import main.structure.LogicStatement
import main.structure.Node
import main.structure.Or

fun (Boolean).lift() : BooleanConstant {
    return BooleanConstant(this)
}

fun (List<LogicStatement>).foldWith(
        constructor: (LogicStatement, LogicStatement) -> LogicStatement
) : LogicStatement {
    return this.foldRight(null as LogicStatement?, { node: LogicStatement, acc: Node? ->
        if (acc == null) return@foldRight node
        return@foldRight constructor(node, acc as LogicStatement)
    })!!
}