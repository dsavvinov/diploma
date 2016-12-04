package test.kotlin

import main.api.EffectSystem
import main.api.dsl.invoke
import main.api.dsl.schema
import main.implementations.operators.Equal
import main.structure.Variable
import main.util.AnyNullType
import main.structure.Function
import main.implementations.operators.Not
import main.util.BooleanType
import main.util.NULL
import main.util.lift
import org.junit.BeforeClass
import org.junit.Test

class ComplexComposition {
    companion object {
        val x = Variable("x", AnyNullType)
        val y = Variable("y", AnyNullType)
        val l = Variable("l", AnyNullType)
        val r = Variable("r", AnyNullType)

        val isNull = Function("isNull", listOf(x), BooleanType)
        val isNotNull = Function("isNotNull", listOf(y), BooleanType)
        val equal = Function("==", listOf(l, r), BooleanType)

        val isNullES = isNull.schema {
            Equal(x, NULL) to Equal(returnVar, true.lift())
            Not(Equal(x, NULL)) to Equal(returnVar, false.lift())
        }

        val isNotNullES = isNotNull.schema {
            Equal(x, NULL) to Equal(returnVar, false.lift())
            Not(Equal(x, NULL)) to Equal(returnVar, true.lift())
        }

        val equalES = equal.schema {
            Equal(l, r) to Equal(returnVar, true.lift())
            Not(Equal(l, r)) to Equal(returnVar, false.lift())
        }

        @BeforeClass
        @JvmStatic
        fun prepare() {
            EffectSystem.addEffectSchema(isNull, isNullES)
            EffectSystem.addEffectSchema(isNotNull, isNotNullES)
            EffectSystem.addEffectSchema(equal, equalES)
        }
    }

    @Test
    fun shouldAlwaysReturnFalse() {
        val z = Variable("z", AnyNullType)
        val call = equal.invoke(
                isNull.invoke(z),
                isNotNull.invoke(z)
        )

        val effects = EffectSystem.inferEffects(call)

        println(effects.print())
    }
}