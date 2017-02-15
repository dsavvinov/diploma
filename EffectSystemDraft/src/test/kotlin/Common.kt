package test.kotlin

import main.api.dsl.defineSchema
import main.implementations.general.EsVariableImpl
import main.structure.*
import main.structure.general.EsFunction
import main.structure.schema.*
import main.structure.schema.operators.Equal
import main.structure.schema.operators.Is

/*
fun assert(value: Boolean) {
    if (!value) {
        throw AssertionError()
    }
}
 */
val assertArg = EsVariableImpl("value", EsBoolean)
val assertFunction: EsFunction = EsFunction("assert", listOf(assertArg), EsUnit)
val assertSchema = assertFunction.defineSchema {
    Equal(assertArg, true.lift()) to Returns(unit, EsUnit)
    Equal(assertArg, false.lift()) to Throws("AssertionFailedException")
}


/*
fun isString(value: Any?) : Boolean = value is String
 */
val isStringArg = EsVariableImpl("value", EsAnyNull)
val isStringFunction = EsFunction("isString", listOf(isStringArg), EsBoolean)
val isStringSchema = isStringFunction.defineSchema {
    Is(isStringArg, EsString)       to Returns(true.lift(), EsBoolean)
    Not(Is(isStringArg, EsString))  to Returns(false.lift(), EsBoolean)
}

/*
fun isBoolean(value: Any?) : Boolean = value is Boolean
 */
val isBooleanArg = EsVariableImpl("value", EsAnyNull)
val isBooleanFunction = EsFunction("isBoolean", listOf(isBooleanArg), EsBoolean)
val isBooleanSchema = isBooleanFunction.defineSchema {
    Is(isBooleanArg, EsBoolean)      to Returns(true.lift(), EsBoolean)
    Not(Is(isBooleanArg, EsBoolean)) to Returns(false.lift(), EsBoolean)
}

/*
fun isZero(val: Int) : Boolean = val == 0
 */
val isZeroArg = EsVariableImpl("val", EsInt)
val isZeroFunction = EsFunction("isZero", listOf(isZeroArg), EsInt)
val isZeroSchema = isZeroFunction.defineSchema {
    Equal(isZeroArg, 0.lift())          to Returns(true.lift(), EsBoolean)
    Not(Equal(isZeroArg, 0.lift()))     to Returns(false.lift(), EsBoolean)
}