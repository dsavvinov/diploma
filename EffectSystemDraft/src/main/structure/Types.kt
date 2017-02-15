package main.structure

import main.implementations.general.EsConstantImpl
import main.structure.general.EsConstant
import main.structure.general.EsType

val EsBoolean = EsType("Boolean")
fun (Boolean).lift() : EsConstant = EsConstantImpl(EsBoolean, this)


val EsUnit = EsType("Unit")
val unit = EsConstantImpl(EsUnit, "unit")

val EsAnyNull = EsType("Any?")

val EsString = EsType("String")
fun (String).lift() : EsConstant = EsConstantImpl(EsString, this)

val EsInt = EsType("Int")
fun (Int).lift() : EsConstant = EsConstantImpl(EsInt, this)