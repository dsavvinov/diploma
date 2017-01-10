package test.kotlin

import main.api.EffectSystem
import main.api.dsl.invoke
import main.api.dsl.schema
import main.implementations.operators.Equal
import main.implementations.operators.Is
import main.implementations.operators.Not
import main.implementations.operators.Throws
import main.structure.*
import main.lang.Function
import main.lang.Variable
import main.util.*
import org.junit.BeforeClass
import org.junit.Test

class HintCompositionTest {
    companion object {
        val x = Variable("x", BooleanType)
        val assertFunction = Function("assert", listOf(x), UnitType)
        val assertES = assertFunction.schema {
            Equal(x, true.lift()) to Equal(returnVar, unit)
            Equal(x, false.lift()) to Throws(AssertionFailedException)
        }

        val y = Variable("x", AnyNullType)

        val isString = Function("isString", listOf(y), BooleanType)
        val isStringES = isString.schema {
            Is(y, StringType) to Equal(returnVar, true.lift())
            Not(Is(y, StringType)) to Equal(returnVar, false.lift())
        }

        @BeforeClass
        @JvmStatic
        fun prepare() {
            EffectSystem.addEffectSchema(isString, isStringES)
            EffectSystem.addEffectSchema(assertFunction, assertES)
        }
    }

    @Test
    fun shouldReturnForString() {
        val s = Variable("s", StringType)

        val callNode = assertFunction.invoke(isString.invoke(s))
        val effects = EffectSystem.inferEffects(callNode)

        println(effects.print())
    }

    @Test
    fun shouldThrowForNonString() {
        val callNode = assertFunction.invoke(isString.invoke(true.lift()))
        val effects = EffectSystem.inferEffects(callNode)

        println(effects.print())
    }
}