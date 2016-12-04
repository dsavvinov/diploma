package main.util

import main.structure.Type

fun (Type).isSubtypeOf(other: Type): Boolean {
    return this.name == other.name
}
