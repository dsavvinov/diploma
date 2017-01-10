package main.lang

interface CallTreeVisitor<out T> {
    fun visit(call: FunctionCall): T
    fun visit(isOperator: IsOperator): T
    fun visit(equalOperator: EqualOperator): T
    fun visit(variable: KtVariable): T
}