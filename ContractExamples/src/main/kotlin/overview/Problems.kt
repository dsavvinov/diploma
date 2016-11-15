package overview

import java.io.Reader
import java.io.Writer
import java.util.*

/**
 * Created by dsavvinov on 28.10.16.
 */

// Possible implementation of overview.peek to demonstrate one of the concepts in forEachCanSpecifyTypeToo
// According to https://github.com/Kotlin/KEEP/issues/47 is gonna be implemented soon
fun <E> Collection<E>.peek(f : (E) -> Unit): Collection<E> {
    return map { f(it); it }
}


// Sometimes we want to know more about functions
// ======= Wrapper-function ===========
fun isNull(obj: Any?) : Boolean {
    return obj == null
}
fun dispatch(obj: Any?) {
    if (isNull(obj)) {
        processNullArg()
        return
    }

    val nonNullObj: Any = obj  // Type mismatch. Required: Any, Found: Any?
    /* Compiler doesn't know that overview.isNull returns true <=> arg is null */
}
// Note that this can became complicated quickly, see EvenNullnessIsHard


// ============= Streams =============
fun filterType(c : Collection<Any>) {
    c.filter { it is String }           // leave only strings
            .map { it.length() }     // still, here compiler fails to infer that any element here is String
}

fun filterTypeWorkaround(c : Collection<Any>) {
    c.filterIsInstance<String>()    // even separate workaround was introduced. Ugly and not flexible!
            .map { it.length }

    // We still can't do following, however:
    c.filter { it is Int || it is Long || it is Short }
            .map { it + 5 }
}

fun peekCanSpecifyTypeToo(c : Collection<Any>) {
    fun functionThatAcceptOnlyStrings(obj: Any) {
        if (obj !is String) {
            throw RuntimeException()
        }
        // ... some computation ...
    }
    c.peek { functionThatAcceptOnlyStrings(it) }.map { it.length() }
    /* Here we want to know function **contract**: it returns successfully iff arg is String */
}
// Summary: we want to know about effects that function produce





// ============ Init in lambda ===============
fun initInLambda() {
    var x: Int
    run {
        x = 2
    }

    x // can't be used here.
    /* Compiler fails to infer that run-lambda is unconditionally invoked! */
}

// Why do we even care about so ivory-esque example? Sane people won't wrap assignments in lambda!
// Well, the point is that a lot of constructs we are accustomed to are imlpemented using lambdas in Kotlin
// Consider:

fun useIdiom(r : Reader?) {
    var i : Int

    r?.use {
        i = r.read()    // Nope, no initialization for you!
    }

    print(i)
}

fun initInLambda2() {
    val someMonitor: Any = Unit
    val x: Int
    synchronized(someMonitor) {
        x = 2 // can't even be initialized!
        /* Compiler not sure how many times will the run-lambda be called, so he conservatively forbid that code,
           even though it's clear that the lambda will be called exactly once
         */
    }
    x
}

// Summary: we want to know which functions will be definitely invoked, so we could apply its effects




// ============= Closures on vars ============
fun indexOfMax(a: IntArray): Int? {
    var maxI: Int? = null
    a.forEachIndexed { i, value ->
        if (maxI == null || value >= a[maxI]) {
            /* Confusing compiler message:
                > Smartcast to 'Int' is impossible, because 'maxI' is a local variable that is captured by a changing closure
               In fact, it's about capturing 'var' - even if it's checked by null, it could be changed back to null by
               another lambda.
               However, that's **not** the case here -- it's not captured by **any** other lambda, and we're perfectly sure
               that it's safe to smartcast it.
             */
            maxI = i
        }
    }
    return maxI
}



// ============ Purity ====================
fun totallyPure() : String? {
    return "totally pure!"
}

fun trivialUseCase() {
    if (totallyPure() != null) {
        totallyPure().length    // could be smart-casted
    }
}

// we could smartcast here if we would explicitly know that `first` is pure function
fun f(pair: Pair<*, *>) {
    if (pair.first !is String) return
    pair.first.length()
}

