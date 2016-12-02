package main.structure

interface Visitor {
    fun visit(call: FunctionCall)
    fun visit(schema: EffectSchema)
    fun visit(effect: Effect)
    fun visit(binaryOperator: BinaryOperator)
    fun visit(logicBinaryOperator: LogicBinaryOperator)
    fun visit(unaryOperator: UnaryOperator)
    fun visit(logicUnaryOperator: LogicUnaryOperator)
    fun visit(primitive: Primitive)
}
