package main.util

import main.lang.Constant
import main.lang.Type

val UnitType = Type("Unit")
val unit = Constant(Unit, UnitType)

val BooleanType = Type("Boolean")

val AnyType = Type("Any")
val AnyNullType = Type("Any?")
val NULL = Constant("null", AnyNullType) // a "little" bit inaccurate definition

val StringType = Type("String")

val AssertionFailedException = main.lang.Exception("AssertionFailedException")