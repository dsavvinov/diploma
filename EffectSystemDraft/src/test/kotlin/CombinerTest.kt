package test.kotlin

import main.api.dsl.*
import main.implementations.general.EsVariableImpl
import main.implementations.visitors.flatten
import main.implementations.visitors.generateEffectSchema
import main.implementations.visitors.print
import main.structure.EsInt
import main.structure.call.CtCall
import main.structure.lift
import main.structure.schema.EffectSchema
import org.junit.Test

class CombinerTest {
    private fun runTest(treeSupplier: () -> CtCall) {
        val callTree = treeSupplier()
        val effectSchema = callTree.generateEffectSchema()
        println(effectSchema.print())

        val combinedSchema = effectSchema.flatten() as EffectSchema
        println(combinedSchema.print())
    }

    @Test
    fun shouldCombineSimpleIs() {
        runTest {
            isStringFunction.invoke("hello".lift())
        }
    }

    @Test
    fun shouldCombineNestedIs() {
        runTest { isBooleanFunction(isStringFunction("hello".lift())) }
    }

    @Test
    fun shouldCombineEqual() {
        runTest { assertFunction(true.lift()) }
    }

    @Test
    fun shouldCombineEqual2() {
        runTest { assertFunction(false.lift()) }
    }

    @Test
    fun shouldCombineNestedEqual() {
        runTest { assertFunction(isZeroFunction(1.lift())) }
    }

    @Test
    fun shouldCombineNestedTwoArgs() {
        runTest { myEqFunction(isZeroFunction(1.lift()), isZeroFunction(0.lift())) }
    }

    @Test
    fun shouldCombineNestedTwoArgs2() {
        runTest { assertFunction(myEqFunction(isZeroFunction(1.lift()), isZeroFunction(0.lift()))) }
    }

    @Test
    fun shouldCombineVar() {
        runTest { assertFunction(isZeroFunction(EsVariableImpl("x", EsInt))) }
    }
}