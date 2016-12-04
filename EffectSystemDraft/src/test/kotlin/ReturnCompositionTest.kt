package test.kotlin

import main.api.EffectSystem
import main.api.dsl.invoke
import main.api.dsl.schema
import main.structure.*
import main.structure.Function
import main.util.*
import org.junit.BeforeClass
import org.junit.Test

class ReturnCompositionTest {
    companion object {
        val x = Variable("x", BooleanType)
        val assertFunction = Function("assert", listOf(x), UnitType)
        val assertES = assertFunction.schema {
            Equal(x, true.lift()) to Equal(returnVar, unit)
            Equal(x, false.lift()) to Throws(AssertionFailedException)
        }

        val y = Variable("x", AnyNullType)

        val isNull = Function("isNull", listOf(y), BooleanType)
        val isNullEs = isNull.schema {
            Equal(y, NULL) to Equal(returnVar, true.lift())
            Not(Equal(y, NULL)) to Equal(returnVar, false.lift())
        }

        @BeforeClass
        @JvmStatic
        fun prepare() {
            EffectSystem.addEffectSchema(assertFunction, assertES)
            EffectSystem.addEffectSchema(isNull, isNullEs)
        }
    }

    @Test
    fun shouldThrowForNonNull() {
        val z = Variable("z", AnyType)
        val callNode: FunctionCall = assertFunction.invoke(isNull.invoke(z))
        val effects = EffectSystem.inferEffects(callNode)

        println(effects.print())
    }

    @Test
    fun shouldReturnForNull() {
        val callNode: FunctionCall = assertFunction.invoke(isNull.invoke(NULL))
        val effects = EffectSystem.inferEffects(callNode)

        println(effects.print())
    }
}