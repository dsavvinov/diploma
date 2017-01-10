package main.lang

data class FunctionCallImpl(override val function: Function, override val args: List<LangNode>) : FunctionCall
data class EqualOperatorImpl(override val left: LangNode, override val right: LangNode) : EqualOperator
data class IsOperatorImpl(override val left: LangNode, override val right: LangNode) : IsOperator
data class VariableImpl(override val name: String, override val type: Type) : Variable
data class ConstantImpl(override val value: Any, override val type: Type) : Constant