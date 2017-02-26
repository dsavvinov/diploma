package test.kotlin

import main.api.dsl.invoke
import main.visitors.generateEffectSchema
import main.visitors.helpers.print
import main.structure.lift
import org.junit.Test

class SubstitutorTests {

    @Test
    fun shouldSubstituteAssertCall() {
        val callTree = assertFunction(true.lift())
        val effectSchema = callTree.generateEffectSchema()

        println(effectSchema.print())
    }

    @Test
    fun shouldSubstituteNestedCall() {
        val callTree = assertFunction(isStringFunction("hello".lift()))
        val effectSchema = callTree.generateEffectSchema()

        println(effectSchema.print())
    }
}