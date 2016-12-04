package main.util

import main.structure.BooleanConstant
import main.structure.Node

fun (Boolean).lift() : BooleanConstant {
    return BooleanConstant(this)
}