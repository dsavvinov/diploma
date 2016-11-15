package overview

import java.util.*

/**
 * Created by dsavvinov on 04.11.16.
 */

fun isNull(obj: Any?) : Boolean {
    return obj == null
    /** Before analysis: Boolean -> arg is Any? **/
    /** After analysis:
     *      True -> arg is null
     *      False -> arg is Any
     */
}
fun dispatch(obj: Any?) {
    if (isNull(obj)) {
        /** We know `overview.isNull` contract:
         *      True -> arg is null
         *      False -> arg is Any
         *  So we know that in `true` branch obj (which is arg) is null
         */
        processNullArg()
        return
    }
    /** And we know that in `false` branch obj is Any */
    val nonNullObj: Any = obj
}


/**
 * Type Hint: Collection<Any> -> Collection<String>
 */
fun filterType(c : Collection<Any>) {
    c.filter { it is String }           // leave only strings
            .map { it.length() }     // still, here compiler fails to infer that any element here is String
}


/**
 * Type Hint = Collection<Any> -> Collection< Int | Long | Short >
 *
 * Probably, will be conformed to common supertype, as Algebraic Types is a pink dream now
 * Type Hint: Collection<Any> -> Collection< Number >
 */
fun filterTypeWorkaround(c : Collection<Any>) {
    // We still can't do following, however:
    c.filter { it is Int || it is Long || it is Short }
            .map { it + 5 }
}

/**
 *  Type Hint = Iterable<T> -> List < Type Hint (predicate) >
 */
public inline fun <T> Iterable<T>.filter(predicate: (T) -> Boolean): List<T> {
    val destination = ArrayList<T>()
    for (element in this) if (predicate(element)) destination.add(element)
    return destination
}

public inline fun <T, C : MutableCollection<in T>> Iterable<T>.filterTo(destination: C, predicate: (T) -> Boolean): C {

}

/**
 * Type Hint = Collection<E> -> Collection < TypeHint overview.f . E >
 */
fun <E> Collection<E>.peek(f : (E) -> Unit): Collection<E> {
    return map { f(it); it }
}


fun peekCanSpecifyTypeToo(c : Collection<Any>) {
    /**
     * Type Hint = Any -> String
     * Side Effect = ( Obj != String) -> Throws RuntimeException
     */
    fun functionThatAcceptOnlyStrings(obj: Any) {
        if (obj !is String) {
            throw RuntimeException()
        }
        // ... some computation ...
    }
    c.peek { functionThatAcceptOnlyStrings(it) }.map { it.length() }
    /* Here we want to know function **contract**: it returns successfully iff arg is String */
}
