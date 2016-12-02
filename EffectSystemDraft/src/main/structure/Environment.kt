package main.structure

import main.implementations.Variable


interface Context {
    operator fun get(variable: Variable): Node
    operator fun set(variable: Variable, evaluatable: Node)
}
