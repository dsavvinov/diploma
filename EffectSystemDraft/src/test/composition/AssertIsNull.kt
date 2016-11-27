package test.composition

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import system.*
import system.Effect.Returns
import system.Effect.Throws
import system.Function
import system.Premise.*

class AssertIsNull {
    val assert = Function("assert")
    val assertArg = Variable("x", UNKNOWN, BOOL)

    val zAnyNull = Variable("z", UNKNOWN, ANY_NULL)
    val yAny = Variable("y", UNKNOWN, ANY)

    val isNullArg: Variable = Variable("x", UNKNOWN, ANY_NULL)
    val isNull: Function = Function("isNull")

    @Before
    fun prepare() {
        val isNullES: EffectSchema = EffectSchemaBuilder().apply {
            addVar(isNullArg)
            addAssertion(Equal(isNullArg, NULL_VAL), Returns(TRUE_VAL))
            addAssertion(NotEqual(isNullArg, NULL_VAL), Returns(FALSE_VAL))
        }.build()

        val assertES = EffectSchemaBuilder().apply {
            addVar(assertArg)

            addAssertion(Equal(assertArg, FALSE_VAL), Throws(ASSERTION_EXCEPTION))
            addAssertion(Equal(assertArg, FALSE_VAL).not(), Returns(UNIT_VAL))

        }.build()

        Context.addEffectSchema(isNull, isNullES)
        Context.addEffectSchema(assert, assertES)
    }

    @Test
    fun returnsUnitForNull() {
        val effects = EffectSystem.inferEffect(Application(assert, mapOf(
                assertArg to Application(isNull, mapOf(
                        isNullArg to NULL
                ))
        ))).assertions

        assertEquals(1, effects.size)
        assertEquals(
                (Const(TRUE) to Returns(UNIT_VAL)).toString(),
                effects[0].toString()
        )
    }

    @Test
    fun throwsForNonNull() {
        val effects = EffectSystem.inferEffect(Application(assert, mapOf(
                assertArg to Application(isNull, mapOf(
                        isNullArg to yAny
                ))
        ))).assertions

        assertEquals(1, effects.size)
        assertEquals(
                (Const(TRUE) to Throws(ASSERTION_EXCEPTION)).toString(),
                effects[0].toString()
        )
    }

    @Test
    fun givesReducedESForUnknown() {
        val effects = EffectSystem.inferEffect(Application(assert, mapOf(
                assertArg to Application(isNull, mapOf(
                        isNullArg to zAnyNull
                ))
        ))).assertions

        assertEquals(2, effects.size)

        assertEquals(
                (Equal(zAnyNull, NULL_VAL) to Returns(UNIT_VAL)).toString(),
                effects[1].toString()
        )

        assertEquals(
                (NotEqual(zAnyNull, NULL_VAL) to Throws(ASSERTION_EXCEPTION)).toString(),
                effects[0].toString()
        )
    }

}