package smartcasts

/**
 * Created by dsavvinov on 05.04.17.
 */

object IntersectionTypes {

    interface A {
        fun foo(): String
    }

    interface B {
        fun bar(): String
    }

    @Effects("""
        t is A -> Returns true;
        t !is A -> Returns false
    """)
    fun isA(t: Any?) = t is A

    @Effects("""
        t is B -> Returns true;
        t !is A -> Returns false
    """)
    fun isB(t: Any?) = t is B

    @Effects("""
        t is A && t is B -> Returns true;
        !(t is A && t is B) -> Returns false
    """)
    fun isAandB(t: Any?) = t is A && t is B


    fun sanityChecks(t: Any?) {
        if (isA(t)) t.foo()

        if (isB(t)) t.bar()
    }

    fun simpleIntersection(t: Any?) {
        if (isAandB(t)) {
            t.foo()
            t.bar()
        }
    }

    fun expressionIntersection(t: Any?) {
        if (isA(t) && isB(t)) {
            t.foo()
            t.bar()
        }
    }
}