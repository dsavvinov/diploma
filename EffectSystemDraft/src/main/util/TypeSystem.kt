package main.util

import main.lang.Type

fun (Type).isSubtypeOf(other: Type): Boolean {
    return this.name == other.name
}
