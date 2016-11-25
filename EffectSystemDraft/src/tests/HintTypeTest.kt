package tests

import system.Application
import system.EffectSystem

/**
 * Created by dsavvinov on 25.11.16.
 */

fun main(args: Array<String>) {
    val effects = EffectSystem.inferEffect(Application(onlyString, mapOf(
            onlyStringArg to zAnyNull
    )))

    print(effects)
}
