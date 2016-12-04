package test.kotlin

import main.api.EffectSystem
import main.api.dsl.invoke
import main.api.dsl.schema
import main.implementations.operators.Equal
import main.implementations.operators.Is
import main.implementations.operators.Not
import main.structure.*
import main.structure.Function
import main.util.*
import org.junit.BeforeClass
import org.junit.Test

class HintTest {
    companion object {
        val x = Variable("x", AnyNullType)

        val isString = Function("isString", listOf(x), BooleanType)
        val isStringES = isString.schema {
            Is(x, StringType) to Equal(returnVar, true.lift())
            Not(Is(x, StringType)) to Equal(returnVar, false.lift())
        }

        @BeforeClass
        @JvmStatic
        fun prepare() {
            EffectSystem.addEffectSchema(isString, isStringES)
        }
    }

    @Test
    fun shouldReturnTrueForString() {
        val s = Variable("s", StringType)

        val callNode: FunctionCall = isString.invoke(s)
        val effects: EffectSchema = EffectSystem.inferEffects(callNode)

        println(effects.print())
    }

    @Test
    fun shouldReturnFalseForNonString() {
        val callNode = isString.invoke(false.lift())
        val effects = EffectSystem.inferEffects(callNode)

        println(effects.print())
    }
}

