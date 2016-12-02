package test.typehint

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import main.system.*
import main.system.Effect.*
import main.system.Function
import main.system.Premise.*

class HintTypeTest {
    val onlyStringArg = Variable("x", UNKNOWN, ANY_NULL)
    val onlyString = Function("onlyString")
    val sString = Variable("s", UNKNOWN, STRING)
    val xFalse = Variable("x", FALSE_VAL, BOOL)
    val zAnyNull = Variable("z", UNKNOWN, ANY_NULL)

    @Before
    fun prepare() {
        val onlyStringES = EffectSchemaBuilder().apply {
            addVar(onlyStringArg)

            addAssertion(
                    Is(onlyStringArg, STRING), Effect.Hints(onlyStringArg, STRING)
            )

            addAssertion(
                    Is(onlyStringArg, STRING).not(), Throws(ILLEGAL_ARGUMENT)
            )
        }.build()
        Context.addEffectSchema(onlyString, onlyStringES)
    }

    @Test
    fun hintsStringForString() {
        val effects = EffectSystem.inferEffect(Application(onlyString, mapOf(
                onlyStringArg to sString
        ))).assertions

        assertEquals(1, effects.size)
        assertEquals( (Const(TRUE) to Hints(sString, STRING)).toString(),
                effects[0].toString())
    }

    @Test
    fun throwsForNonString() {
        val effects = EffectSystem.inferEffect(Application(onlyString, mapOf(
                onlyStringArg to xFalse
        ))).assertions

        assertEquals(1, effects.size)
        assertEquals( (Const(TRUE) to Throws(ILLEGAL_ARGUMENT)).toString(),
                effects[0].toString())
    }

    @Test
    fun givesSchemaForAny() {
        val effects = EffectSystem.inferEffect(Application(onlyString, mapOf(
                onlyStringArg to zAnyNull
        ))).assertions

        assertEquals(2, effects.size)
        assertEquals( (Is(zAnyNull, STRING) to Hints(zAnyNull, STRING)).toString(),
                effects[0].toString())
        assertEquals( (NotIs(zAnyNull, STRING) to Throws(ILLEGAL_ARGUMENT)).toString(),
                effects[1].toString())
    }
}
