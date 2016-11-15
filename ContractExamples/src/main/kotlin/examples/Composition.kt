@file:Suppress("UNREACHABLE_CODE")

package examples

fun isNull(x : Any?) : Boolean {
    return x == null
}

fun isNotNull(x : Any?) : Boolean {
    return x != null
}

fun deadCode() {
    val x : Any? = producer()

    assert (isNull(x) == isNotNull(x))  // never happens, so always fail!
}


// ===========================================

fun churchIf(condition: Boolean, trueBranch : () -> Unit, falseBranch : () -> Unit) {
    if (condition) {
        trueBranch()
    } else {
        falseBranch()
    }
}

fun makePizza() {
    // fires pizzas as side effect. Cool!
}

fun testChurchIf() {
    churchIf(isNotNull(null), { throw AssertionError() }, { makePizza() } )
}


// ==============================================

fun top(x: Any?): Any? {
    if (x is String?) {
        return midString(x)
    } else if (x is Int?) {
        return midInt(x)
    } else if (x is List<*>?) {
        return midList(x)
    } else {
        return null
    }
}

fun midString(x: String?): Int? {
    if (x == null) {
        return null
    } else return x.length
}

fun countWithSideEffect(x : Int): Int {
        
}

fun midInt(x : Int?): Int? {
    if (x == null) {
        return null
    } else return countWithSideEffect(x)
}

fun midList(x : List<*>?): List<Any?>? {
    throwIfNull(x)
    return x.filter { it is String }
}

fun throwIfNull(x: Any?) {
    if (x == null) {
        throw KotlinNullPointerException()
    }
}


fun callSite() {
    top(null) // returns null (doesn't throw!)
    top(5) // returns 6
    top(listOf<Any>(5, "hello", 8, 12, "5", "foo")) // returns List<String>
}