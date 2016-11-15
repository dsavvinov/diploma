@file:Suppress("UNREACHABLE_CODE")

package examples

fun foo(x : Any?) : Boolean {
    return x != null
}

fun producer(): Nothing = TODO()

fun runTest() {
    run {
        foo(null)   // we should know that it is called
    }
}

fun assertTest() {
    assert(foo(null))   // we should know statically that is is 100% AssertionError
}

fun filterTest() {
    val list : List<Any> = producer()

    list.filter { it is String }.map { it.length() }    // we should know that it is a list of strings now
}

fun contractTest() {
    fun bar(t : Any?): Any? {
        if (t == null)
            return null
        return 5
    }

    val k : Any = bar(5)
    // we should at least know that k is not nullable
    // even better if we know that k is Int
}