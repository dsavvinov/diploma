package test.kotlin

import main.api.EffectSystem
import main.api.dsl.invoke
import main.api.dsl.schema
import main.implementations.operators.Equal
import main.implementations.operators.Not
import main.structure.*
import main.lang.Function
import main.lang.Variable
import main.structure.EffectSchema
import main.structure.system.FunctionCall
import main.util.*
import org.junit.BeforeClass
import org.junit.Test

class IsNullTest {
    companion object {
        val x = Variable("x", AnyNullType)

        val isNull = Function("isNull", listOf(x), BooleanType)
        val isNullEs = isNull.schema {
            Equal(x, NULL) to Equal(returnVar, true.lift())
            Not(Equal(x, NULL)) to Equal(returnVar, false.lift())
        }

        @BeforeClass
        @JvmStatic
        fun prepare() {
            EffectSystem.addEffectSchema(isNull, isNullEs)
        }
    }

    @Test
    fun shouldReturnTrueForNull() {
        val callNode: FunctionCall = isNull.invoke(NULL)
        val effects: EffectSchema = EffectSystem.inferEffects(callNode)

        println(effects.print())
    }

    @Test
    fun shouldReturnFalseForNonNull() {
        val callNode: FunctionCall = isNull.invoke(Variable("y", AnyType))
        val effects: EffectSchema = EffectSystem.inferEffects(callNode)

        println(effects.print())
    }
}