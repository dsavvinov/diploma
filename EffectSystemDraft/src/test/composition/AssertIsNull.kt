package test.composition

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import main.system.*
import main.system.Effect.Returns
import main.system.Effect.Throws
import main.system.Function
import main.system.Premise.*

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
            addAssertion(Equal(isNullArg, NULL), Returns(TRUE_VAL))
            addAssertion(NotEqual(isNullArg, NULL), Returns(FALSE_VAL))
        }.build()

        val assertES = EffectSchemaBuilder().apply {
            addVar(assertArg)

            addAssertion(Equal(assertArg, FALSE), Throws(ASSERTION_EXCEPTION))
            addAssertion(Equal(assertArg, FALSE).not(), Returns(UNIT_VAL))

        }.build()

        Context.addEffectSchema(isNull, isNullES)
        Context.addEffectSchema(assert, assertES)
    }

    @Test
    fun returnsUnitForNull() {
        val effects = EffectSystem.inferEffect(Application(assert, mapOf(
                assertArg to Application(isNull, mapOf(
                        isNullArg to NULL
                )).evaluate()
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
                )).evaluate()
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
                )).evaluate()
        ))).assertions

        assertEquals(2, effects.size)

        assertEquals(
                (Equal(zAnyNull, NULL) to Returns(UNIT_VAL)).toString(),
                effects[1].toString()
        )

        assertEquals(
                (NotEqual(zAnyNull, NULL) to Throws(ASSERTION_EXCEPTION)).toString(),
                effects[0].toString()
        )
    }

}