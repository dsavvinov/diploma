package main.implementations.visitors

import main.structure.*

class Evaluator : Visitor {
    override fun visit(call: FunctionCall) {
        TODO("not implemented")
    }

    override fun visit(schema: EffectSchema) {
        TODO("not implemented")
    }

    override fun visit(effect: Effect) {
        TODO("not implemented")
    }

    override fun visit(binaryOperator: BinaryOperator) {
        TODO("not implemented")
    }

    override fun visit(logicBinaryOperator: LogicBinaryOperator) {
        TODO("not implemented")
    }

    override fun visit(unaryOperator: UnaryOperator) {
        TODO("not implemented")
    }

    override fun visit(logicUnaryOperator: LogicUnaryOperator) {
        TODO("not implemented")
    }

    override fun visit(primitive: Primitive) {
        TODO("not implemented")
    }
}