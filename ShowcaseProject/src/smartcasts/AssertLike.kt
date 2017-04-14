package smartcasts

/**
 * Created by dsavvinov on 05.04.17.
 */

object AssertLike {
    @Effects("""
        condition == true -> Returns(unit);
        condition != true -> Throws AssertionError
    """)
    fun myAssert(condition: Boolean) : Unit {
        if (!condition) throw AssertionError("Assertion failed")
    }

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
        left == right -> Returns(true);
        left != right -> Returns(false)
    """)
    fun myEqual(left: Any, right: Any): Boolean = left == right

    fun assertWithSimpleCondition(t: Any?) {
        myAssert(t is String)
        t.length

        myAssert(t !is String)

        t.length  // EXPECTED ERROR.

        // Currently, Data Flow doesn't understand that t can't be both String and !String
        // See example:
        val x: Any? = null
        if (x is String && x !is String) {
            println(x)
            x.length        // Really?
        }
    }

    @Effects("""
        x == 0 -> Returns("zero");
        x == 1 -> Returns(1);
        x != 0 && x != 1 -> Throws InvalidArgumentException
    """)
    fun bar(x: Any?): Any {
        TODO()
    }

    fun equalityHintsToo(t: Any?) {
        myAssert(t == "foobarbaz")
        t.toString()
    }

    val isString = IsStringChecker()

    fun assertWithVariable(t: Any?) {
        myAssert(isString(t))
        val len = t.length
    }



    fun nestedMultiargRandomStuff() {
        val isString = IsStringChecker()
        myAssert(myEqual(isString(bar(1)), isString(bar(0))))

        println("blahblah")
    }
}