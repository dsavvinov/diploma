/**
 * Created by dsavvinov on 28.10.16.
 */

fun processNullArg() { TODO() }

fun processStringArg(str: String) { TODO() }

fun processIntArg(int: Int) { TODO() }

fun dispatch(obj: Any?) {
    // here obj have type "Any?"
    if (obj == null) {
        processNullArg()
        return
    }
    // now obj has type "Any" (we statically know that it can't be null)
    val nonNullObj: Any = obj
    when (nonNullObj) {
        // smartcasted to String
        is String -> processStringArg(nonNullObj)
        // smartcasted to Int
        is Int -> processIntArg(nonNullObj)
        // ...
    }
}
