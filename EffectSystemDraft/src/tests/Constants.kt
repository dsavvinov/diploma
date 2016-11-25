package tests

import system.*
import system.Function
import system.Premise.*
import system.Effect.*

/**
 * Constants for testing purposes. Normally, those should replaced by usual
 * Kotlin types, variables and values; therefore, Kotlin compiler will
 * solve problems of types conformance, equality etc.
 */

/** Types **/
val ANY       = Type("Any")
val ANY_NULL  = Type("Any?")
val BOOL      = Type("Bool")
val BOOL_NULL = Type("Bool?")
val STRING    = Type("String")
val NOTHING   = Type("Nothing")
val UNIT_TYPE = Type("Unit")

/** Literals **/
val TRUE_VAL = Value("TRUE")
val FALSE_VAL = Value("FALSE")
val NULL_VAL = Value("NULL")
val UNIT_VAL = Value("unit")

/** Constants **/
val NULL: Constant = Constant(NULL_VAL, NOTHING)
val TRUE: Constant = Constant(TRUE_VAL, BOOL)
val FALSE: Constant = Constant(FALSE_VAL, BOOL)
val UNIT = Constant(UNIT_VAL, UNIT_TYPE)

val UNKNOWN = Value("???")


/** Variables **/
val yAny = Variable("y", UNKNOWN, ANY)
val zAnyNull = Variable("z", UNKNOWN, ANY_NULL)
val xTrue = Variable("x", TRUE_VAL, BOOL)
val xFalse = Variable("x", FALSE_VAL, BOOL)
val sString = Variable("x", UNKNOWN, STRING)

/** Exceptions **/
val ASSERTION_EXCEPTION = Exception("AssertionError")
val ILLEGAL_ARGUMENT = Exception("IllegalArgumentException")

/** Effect schemas for some functions **/

/*
    fun isNull(x: Any?) {
        return x == null
    }
 */
val isNullArg: Variable = Variable("x", UNKNOWN, ANY_NULL)
val isNull: Function = Function("isNull")
val isNullES: EffectSchema = EffectSchema().apply {
    addVar(isNullArg)
    addAssertion(Equal(isNullArg, NULL_VAL), Returns(TRUE_VAL))
    addAssertion(Equal(isNullArg, NULL_VAL).not(), Returns(FALSE_VAL))
    Context.addEffectSchema(isNull, this)
}


/*
    fun assert(x: Boolean) {
        if (!x) throw AssertionException()
    }
 */
val assertArg = Variable("x", UNKNOWN, BOOL)
val assert = Function("assert")
val assertES = EffectSchema().apply {
    addVar(assertArg)

    addAssertion(Equal(assertArg, FALSE_VAL), Throws(ASSERTION_EXCEPTION))
    addAssertion(Equal(assertArg, TRUE_VAL).not(), Returns(UNIT_VAL))

    Context.addEffectSchema(assert, this)
}


/*
    onlyString(x: Any?) {
        if (x !is String) throw IllegalArgumentException()
    }
 */
val onlyStringArg = Variable("x", UNKNOWN, ANY_NULL)
val onlyString = Function("onlyString")
val onlyStringES = EffectSchema().apply {
    addVar(onlyStringArg)

    addAssertion(
            Is(onlyStringArg, STRING), Hints(onlyStringArg, STRING)
    )

    addAssertion(
            Is(onlyStringArg, STRING).not(), Throws(ILLEGAL_ARGUMENT)
    )

    Context.addEffectSchema(onlyString, this)
}