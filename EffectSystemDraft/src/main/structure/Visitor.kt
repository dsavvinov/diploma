package main.structure

// TODO: think about selective visitor
interface Visitor {
    fun visit(call: FunctionCall): Node
    fun visit(schema: EffectSchema): EffectSchema
    fun visit(effect: Effect): Effect

    fun visit(isOperator: Is): Node
    fun visit(equalOperator: Equal): Node
    fun visit(throwsOperator: Throws): Throws

    fun visit(or: Or): Node
    fun visit(and: And): Node
    fun visit(not: Not): Node

    fun visit(exception: Exception): Exception
    fun visit(type: Type): Type
    fun visit(variable: Variable): Variable
    fun visit(function: Function): Function
    fun visit(booleanConstant: BooleanConstant): BooleanConstant
}
