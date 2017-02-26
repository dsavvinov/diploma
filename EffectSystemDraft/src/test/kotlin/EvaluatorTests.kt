package test.kotlin

import main.api.dsl.invoke
import main.visitors.evaluate
import main.visitors.flatten
import main.visitors.generateEffectSchema
import main.visitors.helpers.print
import main.structure.EsInt
import main.structure.call.CtCall
import main.structure.general.EsVariable
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
    fun shouldEvaluateSimpleIs() {
        runTest { isStringFunction.invoke("hello".lift()) }
    }

    @Test
    fun shouldEvaluateNestedIs() {
        runTest { isBooleanFunction(isStringFunction("hello".lift())) }
    }

    @Test
    fun shouldEvaluateEqual() {
        runTest { assertFunction(true.lift()) }
    }

    @Test
    fun shouldEvaluateEqual2() {
        runTest { assertFunction(false.lift()) }
    }

    @Test
    fun shouldEvaluateNestedEqual() {
        runTest { assertFunction(isZeroFunction(1.lift())) }
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
    fun shouldEvaluateVar() {
        runTest { assertFunction(isZeroFunction(EsVariable("x", EsInt))) }
    }
}