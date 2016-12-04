package main.structure

// TODO: think about selective visitor
interface Visitor {
    fun visit(call: FunctionCall): Node
    fun visit(schema: EffectSchema): EffectSchema
    fun visit(effect: Effect): Effect

    fun visit(isOperator: Is): LogicStatement
    fun visit(equalOperator: Equal): LogicStatement
    fun visit(throwsOperator: Throws): Throws

    fun visit(or: Or): LogicStatement
    fun visit(and: And): LogicStatement
    fun visit(not: Not): LogicStatement

    fun visit(exception: Exception): Exception
    fun visit(type: Type): Type
    fun visit(variable: Variable): Node
    fun visit(function: Function): Function
    fun visit(constant: Constant): Constant
    fun visit(booleanConstant: BooleanConstant): BooleanConstant
}
