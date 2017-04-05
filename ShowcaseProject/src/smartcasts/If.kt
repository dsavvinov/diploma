package smartcasts

/**
 * Created by dsavvinov on 05.04.17.
 */

object If {
    class IsStringChecker {
        @Effects("""
            arg is String -> Returns true;
            arg !is String -> Returns false
        """)
        operator fun invoke(arg: Any?): Boolean {
            return arg is String
        }
    }

    @Effects("""
        a !is String -> Returns true;
        a is String -> Returns false
    """)
    fun notIsString(a: Any?) = a !is String

    val isString = IsStringChecker()

    fun ifSimpleCondition(t: Any?) {
        if (isString(t)) t.length else t.toString()

        if (notIsString(t)) t.toString() else t.length
    }

    fun ifExpression(t: Any?) {
        if (!isString(t)) t.toString() else t.length

        if (!notIsString(t)) t.length else t.toString()
    }
}