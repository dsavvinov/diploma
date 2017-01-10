package main.structure

import main.lang.Function
import main.lang.Variable


interface Context : MutableMap<Variable, Node> {
    fun getFunctionSchema(function: Function): EffectSchema
}
