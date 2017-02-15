package test.kotlin

import main.api.dsl.*
import main.implementations.visitors.evaluate
import main.implementations.visitors.generateEffectSchema
import main.implementations.visitors.print
import main.structure.lift
import main.structure.schema.EffectSchema
import org.junit.Test

class EvaluatorTest {
    @Test
    fun shouldEvaluateSimpleIs() {
        val callTree = isStringFunction("hello".lift())
        val effectSchema = callTree.generateEffectSchema()
        println(effectSchema.print())

        val evaluatedSchema = effectSchema.evaluate() as EffectSchema
        println(evaluatedSchema.print())
    }

    @Test
    fun shouldEvaluateNestedIs() {
        val callTree = isBooleanFunction(isStringFunction("hello".lift()))
        val effectSchema = callTree.generateEffectSchema()
        println(effectSchema.print())

        val evaluatedSchema = effectSchema.evaluate() as EffectSchema
        println(evaluatedSchema.print())
    }

    @Test
    fun shouldEvaluateEqual() {
        val callTree = assertFunction(true.lift())
        val effectSchema = callTree.generateEffectSchema()
        println(effectSchema.print())

        val evaluatedSchema = effectSchema.evaluate() as EffectSchema
        println(evaluatedSchema.print())
    }

    @Test
    fun shouldEvaluateEqual2() {
        val callTree = assertFunction(false.lift())
        val effectSchema = callTree.generateEffectSchema()
        println(effectSchema.print())

        val evaluatedSchema = effectSchema.evaluate() as EffectSchema
        println(evaluatedSchema.print())
    }

    @Test
    fun shouldEvaluateNestedEqual() {
        val callTree = assertFunction(isZeroFunction(1.lift()))
        val es = callTree.generateEffectSchema()
        println(es.print())

        val evEs = es.evaluate() as EffectSchema
        println(evEs.print())
    }

}