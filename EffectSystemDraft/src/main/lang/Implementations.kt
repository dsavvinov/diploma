package main.lang

import main.structure.Variable

data class FunctionCallImpl(override val function: Function, override val args: List<KtNode>) : FunctionCall
data class EqualOperatorImpl(override val left: KtNode, override val right: KtNode) : EqualOperator
data class IsOperatorImpl(override val left: KtNode, override val right: KtNode) : IsOperator
data class VariableImpl(override val name: String, override val type: Type) : Variable
