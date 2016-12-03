package main.implementations

import main.structure.Context
import main.structure.Node
import main.structure.Variable

data class ContextMapImpl(val ctx: MutableMap<Variable, Node>) : Context {
    override fun get(variable: Variable): Node? {
        return ctx[variable]
    }

    override fun set(variable: Variable, substitution: Node) {
        ctx[variable] = substitution
    }
}
