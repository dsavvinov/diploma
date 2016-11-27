package test.trivial

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import system.*
import system.Effect.Returns
import system.Effect.Throws
import system.Function
import system.Premise.*

class AssertTest {
    val assert = Function("assert")
    val assertArg = Variable("x", UNKNOWN, BOOL)
    val zAnyNull = Variable("z", UNKNOWN, ANY_NULL)

    @Before
    fun prepare() {
        /*
        fun assert(x: Boolean) {
            if (!x) throw AssertionException()
        }
        */
        val assertES = EffectSchemaBuilder().apply {
            addVar(assertArg)

            addAssertion(Equal(assertArg, FALSE_VAL), Throws(ASSERTION_EXCEPTION))
            addAssertion(Equal(assertArg, FALSE_VAL).not(), Returns(UNIT_VAL))

        }.build()

        Context.addEffectSchema(assert, assertES)
    }

    @Test
    fun assertTrueReturnsUnit() {
        val effects = EffectSystem.inferEffect(Application(assert, mapOf(
                assertArg to xTrue
        ))).assertions

        assertEquals(1, effects.size)
        assertEquals(
                (Const(TRUE) to Returns(UNIT_VAL)).toString(),
                effects[0].toString()
        )
    }

    @Test
    fun assertFalseThrows() {
        val effects = EffectSystem.inferEffect(Application(assert, mapOf(
                assertArg to xFalse
        ))).assertions

        assertEquals(1, effects.size)
        assertEquals(
                (Const(TRUE) to Throws(ASSERTION_EXCEPTION)).toString(),
                effects[0].toString()
        )
    }

    @Test
    fun assertUnknownProducesES() {
        val effects = EffectSystem.inferEffect(Application(assert, mapOf(
                assertArg to zAnyNull
        ))).assertions

        assertEquals(2, effects.size)
        assertEquals(
                (Equal(zAnyNull, FALSE_VAL) to Throws(ASSERTION_EXCEPTION)).toString(),
                effects[0].toString()
        )
        assertEquals(
                (NotEqual(zAnyNull, FALSE_VAL) to Returns(UNIT_VAL)).toString(),
                effects[1].toString()
        )
    }
}