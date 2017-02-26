package test.kotlin

import main.api.dsl.invoke
import main.facade.EffectSystem
import main.facade.EsInfoHolderImpl
import main.implementations.general.EsVariableImpl
import main.structure.*
import main.structure.schema.effects.Returns
import main.structure.schema.effects.Throws
import org.junit.Test
import org.junit.Assert.*

class EffectSystemTests {

    @Test
    fun alwaysFeasible() {
        val info = EffectSystem.getInfo(
                isStringFunction.invoke("hello".lift()),
                Returns(true.lift(), EsBoolean)
        ) as EsInfoHolderImpl

        // Doesn't impose any restrictions on the context
        assertTrue(info.varsTypes.isEmpty())
        assertTrue(info.varsValues.isEmpty())
    }

    @Test
    fun alwaysFeasibleNested() {
        val info = EffectSystem.getInfo(
                isBooleanFunction(isStringFunction("hello".lift())),
                Returns(true.lift(), EsBoolean)
        ) as EsInfoHolderImpl

        // Doesn't impose any restrictions on the context
        assertTrue(info.varsTypes.isEmpty())
        assertTrue(info.varsValues.isEmpty())
    }

    @Test
    fun alwaysThrows() {
        val info = EffectSystem.getInfo(
                assertFunction(false.lift()),
                Returns(unit, EsUnit)
        ) as EsInfoHolderImpl?

        assertEquals(null, info)

        val info2 = EffectSystem.getInfo(
                assertFunction(false.lift()),
                Throws("AssertionFailedException")
        ) as EsInfoHolderImpl

        assertTrue(info2.varsTypes.isEmpty())
        assertTrue(info2.varsValues.isEmpty())
    }

    @Test
    fun alwaysThrowsDeeplyNested() {
        val info = EffectSystem.getInfo(
                assertFunction(myEqFunction(isZeroFunction(1.lift()), isZeroFunction(0.lift()))),
                Returns(unit, EsUnit)
        ) as EsInfoHolderImpl?

        assertEquals(null, info)

        val info2 = EffectSystem.getInfo(
                assertFunction(myEqFunction(isZeroFunction(1.lift()), isZeroFunction(0.lift()))),
                Throws("AssertionFailedException")
        ) as EsInfoHolderImpl

        assertTrue(info2.varsTypes.isEmpty())
        assertTrue(info2.varsValues.isEmpty())
    }

    @Test
    fun infersSimpleValueCondition() {
        val variable = EsVariableImpl("x", EsInt)
        val info = EffectSystem.getInfo(
                assertFunction(isZeroFunction(variable)),
                Returns(unit, EsUnit)
        ) as EsInfoHolderImpl

        assertEquals(1, info.varsValues.size)
        assertEquals(1, info.varsTypes.size)

        assertEquals(EsInt, info.getVariableType(variable))
        assertEquals(0.lift(), info.getVariableValue(variable))
    }
}