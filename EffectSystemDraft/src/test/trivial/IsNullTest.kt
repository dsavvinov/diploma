package test.trivial

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import main.system.*
import main.system.Effect.Returns
import main.system.Effect.Throws
import main.system.Function
import main.system.Premise.*

class IsNullTest {
    val isNullArg: Variable = Variable("x", UNKNOWN, ANY_NULL)
    val isNull: Function = Function("isNull")
    val zAnyNull = Variable("z", UNKNOWN, ANY_NULL)

    @Before
    fun prepare() {
        val isNullES: EffectSchema = EffectSchemaBuilder().apply {
            addVar(isNullArg)
            addAssertion(Equal(isNullArg, NULL), Returns(TRUE_VAL))
            addAssertion(NotEqual(isNullArg, NULL), Returns(FALSE_VAL))
        }.build()
        Context.addEffectSchema(isNull, isNullES)
    }

    @Test
    fun trueForNull() {
        val effects = EffectSystem.inferEffect(Application(isNull, mapOf(
                isNullArg to NULL
        ))).assertions

        assertEquals(1, effects.size)
        assertEquals( (Const(TRUE) to Returns(TRUE_VAL)).toString(),
                effects[0].toString())
    }

    @Test
    fun falseForNonNull() {
        val effects = EffectSystem.inferEffect(Application(isNull, mapOf(
                isNullArg to yAny
        ))).assertions

        assertEquals(1, effects.size)
        assertEquals( (Const(TRUE) to Returns(FALSE_VAL)).toString(),
                effects[0].toString())
    }

    @Test
    fun schemaForUnknown() {
        val effects = EffectSystem.inferEffect(Application(isNull, mapOf(
                isNullArg to zAnyNull
        ))).assertions

        assertEquals(2, effects.size)
        assertEquals( (Equal(zAnyNull, NULL) to Returns(TRUE_VAL)).toString(),
                effects[0].toString())
        assertEquals( (NotEqual(zAnyNull, NULL) to Returns(FALSE_VAL)).toString(),
                effects[1].toString())
    }
}