import java.io.OutputStreamWriter
import java.io.Reader
import java.io.Writer
import java.util.*

/**
 * Created by dsavvinov on 28.10.16.
 */

// Possible implementation of peek to demonstrate one of the concepts in forEachCanSpecifyTypeToo
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
    /* Compiler doesn't know that isNull returns true <=> arg is null */
}
// Note that this can became complicated quickly, see EvenNullnessIsHard


// ============= Streams =============
fun filterType(c : Collection<Any>) {
    c.filter { it is String }
            // unresolved reference: length
            .map { it.length() }
}

fun filterTypeWorkaround(c : Collection<Any>) {
    c.filterIsInstance<String>()
            .map { it.length }
}

fun peekHints(c : Collection<Any>) {
    fun foo(obj: Any) {
        if (obj !is String) {
            throw RuntimeException()
        }
        // ... some computation ...
    }
    c.peek { foo(it) }.map { it.length() }
}
// Summary: we want to know about effects that function produce





// ============ Init in lambda ===============
fun initInLambda() {
    var x: Int
    run {
        x = 2
    }
    x // Variable 'x' must be initialized
}

// Why do we even care about so ivory-esque example? Sane people won't wrap assignments in lambda!
// Well, the point is that a lot of constructs we are accustomed to are imlpemented using lambdas in Kotlin
// Consider:

fun useIdiom(r : Reader) {
    val i : Int
    OutputStreamWriter(r.getOutputSteam).use {
        // Captured values initialization is forbidden due to
        // possible reassignment
        i = it.read()
    }
    print(i)
}

class Bar {
    var x : Int
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
            // Smartcast to 'Int' is impossible, because 'maxI'
            // is a local variable that is captured by a
            // changing closure.
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
    // Smart cast to 'String' is impossible, because 'pair.first'
    // is a public API property declared in different module
    pair.first.length()
}

