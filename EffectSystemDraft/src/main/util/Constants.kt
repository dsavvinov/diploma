package main.util

import main.structure.Constant
import main.structure.Type

val UnitType = Type("Unit")
val unit = Constant(Unit, UnitType)

val BooleanType = Type("Boolean")

val AnyType = Type("Any")
val AnyNullType = Type("Any?")
val NULL = Constant("null", AnyNullType) // a "little" bit inaccurate definition

val AssertionFailedException = main.structure.Exception("AssertionFailedException")