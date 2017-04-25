package smartcasts

/**
 * Created by dsavvinov on 05.04.17.
 */

object If {
    class IsStringChecker {
        @Effects("""
            arg is String -> Returns(true);
            arg !is String -> Returns(false)
        """)
        operator fun invoke(arg: Any?): Boolean {
            return arg is String
        }
    }

    @Effects("""
        a !is String -> Returns(true);
        a is String -> Returns(false)
    """)
    fun notIsString(a: Any?) = a !is String

    val isString = IsStringChecker()

    @Effects("""
        x is Int -> Returns(true);
        x !is Int -> Returns(false)
    """)
    fun isInt(x: Any?) = x is Int

    fun ifSimpleCondition(t: Any?) {
        if (isString(t)) t.length else t.toString()

        if (notIsString(t)) t.toString() else t.length
    }

    fun ifExpression(t: Any?) {
        if (!isString(t)) t.toString() else t.length

        if (!notIsString(t)) t.length else t.toString()
    }

    fun ifExpression2(x: Any?, y: Any?) {
        if (isString(x) && isInt(y)) {
            x.length
            y.and(5)
        } else {
            // EXPECTED ERRORS
            x.length
            x.and(5)
            y.and(5)
            y.length
        }
    }

    fun orExpression(x: Any?, y: Any?) {
        if (isString(x) || isInt(y)) {
            // EXPECTED ERRORS
            x.length
            x.and(5)
            y.and(5)
            y.length
        } else {
            // EXPECTED ERRORS
            x.length
            x.and(5)
            y.and(5)
            y.length
        }
    }

    fun deMorgan(x: Any?, y: Any?) {
        if (notIsString(x) || !isInt(y)) {
            // EXPECTED ERRORS
            x.length
            x.and(5)
            y.and(5)
            y.length
        } else {
            x.length
            x.and(5)    // EXPECTED ERROR

            y.and(5)
            y.length    // EXPECTED ERROR
        }
    }
}