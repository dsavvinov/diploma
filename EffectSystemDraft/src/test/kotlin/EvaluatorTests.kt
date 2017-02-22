package test.kotlin

import main.api.dsl.invoke
import main.implementations.general.EsVariableImpl
import main.implementations.visitors.flatten
import main.implementations.visitors.evaluate
import main.implementations.visitors.generateEffectSchema
import main.implementations.visitors.helpers.print
import main.structure.EsInt
import main.structure.call.CtCall
import main.structure.lift
import main.structure.schema.EffectSchema
import org.junit.Test

class EvaluatorTests {
    private fun runTest(treeSupplier: () -> CtCall) {
        val callTree = treeSupplier()
        val es = callTree.generateEffectSchema()
        println(es.print())

        val flatEs = es.flatten() as EffectSchema
        println(flatEs.print())

        val evEs = flatEs.evaluate() as EffectSchema
        println(evEs.print())
    }

    @Test
    fun shouldEvaluateNestedTwoArgs() {
        runTest { myEqFunction(isZeroFunction(1.lift()), isZeroFunction(0.lift())) }
    }

    @Test
    fun shouldEvaluateNestedTwoArgs2() {
        runTest { assertFunction(myEqFunction(isZeroFunction(1.lift()), isZeroFunction(0.lift()))) }
    }

    @Test
    fun shouldEvaluateVariables() {
        val x = EsVariableImpl("x", EsInt)
        runTest { myEqFunction(isZeroFunction(x), notZeroFunction(x)) }
    }
}