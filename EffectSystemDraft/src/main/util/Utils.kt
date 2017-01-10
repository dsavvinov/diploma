package main.util

import main.lang.BooleanConstant
import main.structure.Node

fun (Boolean).lift() : BooleanConstant {
    return BooleanConstant(this)
}