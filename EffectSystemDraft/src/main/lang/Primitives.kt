package main.lang

import main.structure.Variable

data class Type(val name: String)

data class Function(val name: String, val parameters: List<Variable>, val returnType: Type) {
    val returnVar: Variable = VariableImpl("return_$name", returnType)
}