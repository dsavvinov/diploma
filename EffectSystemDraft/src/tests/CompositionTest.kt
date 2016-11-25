package tests

import system.Application
import system.EffectSystem

/**
 * Created by dsavvinov on 25.11.16.
 */

fun main(args: Array<String>) {
    val effects = EffectSystem.inferEffect(Application(assert, mapOf(
            assertArg to Application(isNull, mapOf(
                    isNullArg to zAnyNull
            ))
    )))

    print(effects)
}
