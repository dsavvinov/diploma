package main.structure

import main.structure.general.EsConstant
import main.structure.general.EsType

/**
 * Kotlin types lifted into Effect System.
 * Probably, this file is temporary and will removed as Effect System
 * will be integrated with compiler (then, EsType will be changed into KtType).
 */
val EsBoolean = EsType("Boolean")
fun (Boolean).lift() : EsConstant = EsConstant(EsBoolean, this)


val EsUnit = EsType("Unit")
val unit = EsConstant(EsUnit, "unit")

val EsAnyNull = EsType("Any?")

val EsString = EsType("String")
fun (String).lift() : EsConstant = EsConstant(EsString, this)

val EsInt = EsType("Int")
fun (Int).lift() : EsConstant = EsConstant(EsInt, this)