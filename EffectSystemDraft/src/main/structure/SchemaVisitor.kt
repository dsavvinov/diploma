package main.structure

// TODO: think about selective visitor
// TODO: returns Data, where Data is generic arg
interface SchemaVisitor<out T> {
    fun visit(schema: EffectSchema): T

    fun visit(effect: Effect): T

    fun visit(operator: Operator): T

    fun visit(variable: Variable): T
}
