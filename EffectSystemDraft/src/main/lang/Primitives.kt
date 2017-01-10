package main.lang

data class Type(val name: String)

data class Function(val name: String, val arguments: List<Variable>, val returnType: Type) {
    val returnVar: Variable = VariableImpl("return_$name", returnType)
}