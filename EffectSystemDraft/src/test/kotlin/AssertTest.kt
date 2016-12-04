package test.kotlin

import main.api.EffectSystem
import main.api.dsl.invoke
import main.api.dsl.schema
import main.structure.*
import main.structure.Function
import main.util.*
import org.junit.BeforeClass
import org.junit.Test

class AssertTest {
    companion object {
        val x = Variable("x", BooleanType)
        val assertFunction = Function("assert", listOf(x), UnitType)
        val assertES = assertFunction.schema {
            Equal(x, true.lift()) to Equal(returnVar, unit)
            Equal(x, false.lift()) to Throws(AssertionFailedException)
        }

        @BeforeClass
        @JvmStatic
        fun prepare() {
            EffectSystem.addEffectSchema(assertFunction, assertES)
        }
    }

    @Test
    fun shouldThrowForFalse() {
        val callNode: FunctionCall = assertFunction.invoke(false.lift())
        val effects: EffectSchema = EffectSystem.inferEffects(callNode)

        println(effects.print())
    }

    @Test
    fun shouldReturnForTrue() {
        val callNode: FunctionCall = assertFunction.invoke(true.lift())
        val effects: EffectSchema = EffectSystem.inferEffects(callNode)

        println(effects.print())
    }

    @Test
    fun shouldReturnSchemaForUnknown() {
        val callNode: FunctionCall = assertFunction.invoke(Variable("y", BooleanType))
        val effects: EffectSchema = EffectSystem.inferEffects(callNode)

        println(effects.print())
    }
}