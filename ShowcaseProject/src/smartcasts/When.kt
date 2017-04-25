package smartcasts

/**
 * Created by dsavvinov on 05.04.17.
 */

object When {
    @Effects("""
        x == null -> Returns("blabla");
        x != null -> Returns(2)
    """)
    fun foo(x: Int?): Any? {
        x ?: return "Wrong Input"
        return 2
    }

    @Effects("""
        t is String -> Returns("");
        t is Int -> Returns(0);
        !(t is String && t is Int) -> Throws IllegalArgumentException
    """)
    fun getDefaultValue(t: Any?): Any? {
        if (t is String) return ""
        if (t is Int) return 0
        else throw IllegalArgumentException("")
    }

    @Effects("""true -> Returns("") """)
    fun getStringDefaultValue() = ""

    @Effects("true -> Returns(0)")
    fun getIntDefaultValue() = 0

    fun whenSimpleIs(t: Int?) {
        when(foo(t)) {
            is String -> t.toString()
            is Int -> t.and(5)
            // note: `else` not working
        }
    }

    fun whenSimpleCondition(t: Int?) {
        when(foo(t)) {
            "blabla" -> println(t)
            2 -> t.and(5)
        // We understand here that we have enumerated all posibilities, so in theory, we can report exhaustive When
        }

        when (foo(t)) {
            "blabla" -> println(t)
            5 -> t.and(5)       // EXPECTED ERROR
        }
    }

    fun whenExpressionCondition(t: Any?) {
        when(getDefaultValue(t)) {
            getIntDefaultValue() -> {
                t.and(0)
                t.length        // EXPECTED ERROR
            }
            getStringDefaultValue() -> {
                t.length
                t.and(0)    // EXPECTED ERROR
            }
        }
    }
}