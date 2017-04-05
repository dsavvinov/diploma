package smartcasts

/**
 * Created by dsavvinov on 05.04.17.
 */

object When {
    // TODO: add types to the possible arg of Returns in grammar
    @Effects("""
        x == null -> Returns "blabla";
        x != null -> Returns 2
    """)
    fun foo(x: Int?): Any? {
        x ?: return "Wrong Input"
        return 2
    }

    @Effects("""
        t is String -> Returns "";
        t is Int -> Returns 0;
        !(t is String && t is Int) -> Throws IllegalArgumentException
    """)
    fun getDefaultValue(t: Any?): Any? {
        if (t is String) return ""
        if (t is Int) return 0
        else throw IllegalArgumentException("")
    }

    @Effects("""true -> Returns "" """)
    fun getStringDefaultValue() = ""

    @Effects("true -> Returns 0")
    fun getIntDefaultValue() = 0

    fun whenSimpleIs(t: Int?) {
        when(foo(t)) {
            is String -> t.toString()
            else -> t.toString()
        }
    }

    fun whenSimpleCondition(t: Int?) {
        when(foo(t)) {
            "blabla" -> t.toString()
            2 -> t.toString()
        // We understand here that we have enumerated all posibilities, so in theory, we can report exhaustive When
        }
    }

    fun whenExpressionCondition(t: Any?) {
        when(getDefaultValue(t)) {
            getStringDefaultValue() -> t.length
            getIntDefaultValue() -> t.and(0)
        }
    }
}