package tests

import system.Application
import system.EffectSystem

fun main(args: Array<String>) {
    val effects = EffectSystem.inferEffect(Application(assert, mapOf(
            assertArg to yAny
    )))

    print(effects)
}