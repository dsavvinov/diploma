package main.system

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
val NOTHING_NULL = Type("Nothing?")
val UNIT_TYPE = Type("Unit")

/** Literals **/
val TRUE_VAL = Value("TRUE")
val FALSE_VAL = Value("FALSE")
val NULL_VAL = Value("NULL")
val UNIT_VAL = Value("unit")

/** Constants **/
val NULL: Constant = Constant(NULL_VAL, NOTHING_NULL)
val TRUE: Constant = Constant(TRUE_VAL, BOOL)
val FALSE: Constant = Constant(FALSE_VAL, BOOL)
val UNIT = Constant(UNIT_VAL, UNIT_TYPE)

val UNKNOWN = Value("???")


/** Variables **/
val yAny = Variable("y", UNKNOWN, ANY)

val xTrue = Variable("x", TRUE_VAL, BOOL)
val xFalse = Variable("x", FALSE_VAL, BOOL)

/** Exceptions **/
val ASSERTION_EXCEPTION = Exception("AssertionError")
val ILLEGAL_ARGUMENT = Exception("IllegalArgumentException")
