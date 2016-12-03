package main.structure


interface Context {
    operator fun get(variable: Variable): Node?
    operator fun set(variable: Variable, substitution: Node)
}
