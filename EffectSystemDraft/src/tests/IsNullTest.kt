package tests

import system.*
import system.Function

/**
 * Created by dsavvinov on 25.11.16.
 */

fun main(args: Array<String>) {
    val effects = EffectSystem.inferEffect(Application(isNull, mapOf(
            isNullArg to zAnyNull
    )))

    print(effects)
}
