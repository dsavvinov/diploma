///**
// * Created by dsavvinov on 29.03.17.
// */
//
//
//object Smartcasts {
//    // ==============================================
//    // Definitions
//
//    class IsStringChecker {
//        @Effects("""
//            arg is String -> Returns true;
//            arg !is String -> Returns false
//        """)
//        operator fun invoke(arg: Any?): Boolean {
//            return arg is String
//        }
//    }
//
//    @Effects("""
//        condition == true -> Returns unit;
//        condition != true -> Throws AssertionError
//    """)
//    fun myAssert(condition: Boolean) : Unit {
//        if (!condition) throw AssertionError("Assertion failed")
//    }
//
//    @Effects("""
//        left == right -> Returns true;
//        left != right -> Returns false
//    """)
//    fun myEqual(left: Any, right: Any): Boolean = left == right
//
//    @Effects("""
//        x == 0 -> Returns "zero";
//        x == 1 -> Returns 1;
//        x != 0 && x != 1 -> Throws InvalidArgumentException
//    """)
//    fun bar(x: Int): Any {
//        TODO()
//    }
//
//    @Effects("""
//        a !is String -> Returns true;
//        a is String -> Returns false
//    """)
//    fun notIsString(a: Any?) = a !is String
//
//    val isString = IsStringChecker()
//    // ===================================================
//    // Assert-like
//
//    fun assertWithSimpleCondition(t: Any?) {
//        myAssert(t is String)
//
//        t.length
//    }
//
//
//    fun equalityHintsToo(t: Any?) {
//        myAssert(t == "foobarbaz")
//
//        t.toString()
//    }
//
//
//
//    fun assertWithVariable(t: Any?) {
//        myAssert(isString(t))
//
//        val len = t.length
//    }
//
//
//
//    fun nestedMultiargRandomStuff() {
//        val isString = IsStringChecker()
//        myAssert(myEqual(
//                left = isString(bar(8)),
//                right = isString(bar(0))
//        ))
//
//        println("blahblah")
//    }
//
//
//    // ===================================================
//    // If-expression
//
//    fun ifSimpleCondition(t: Any?) {
//        if (isString(t)) t.length else t.toString()
//
//        if (notIsString(t)) t.toString() else t.length
//    }
//
//    fun ifExpression(t: Any?) {
//        if (!isString(t)) t.toString() else t.length
//
//        if (!notIsString(t)) t.length else t.toString()
//    }
//
//
//    //=================================================
//    // Intersection types
//
//    interface A { fun foo(): String }
//    interface B { fun bar(): String }
//
//    @Effects("""
//        t is A -> Returns true;
//        t !is A -> Returns false
//    """)
//    fun isA(t: Any?) = t is A
//
//    @Effects("""
//        t is B -> Returns true;
//        t !is A -> Returns false
//    """)
//    fun isB(t: Any?) = t is B
//
//    @Effects("""
//        t is A && t is B -> Returns true;
//        !(t is A && t is B) -> Returns false
//    """)
//    fun isAandB(t: Any?) = t is A && t is B
//
//
//    fun sanityChecks(t: Any?) {
//        if (isA(t)) t.foo()
//
//        if (isB(t)) t.bar()
//    }
//
//    fun simpleIntersection(t: Any?) {
//        if (isAandB(t)) {
//            t.foo()
//            t.bar()
//        }
//    }
//
//    fun expressionIntersection(t: Any?) {
//        if (isA(t) && isB(t)) {
//            t.foo()
//            t.bar()
//        }
//    }
//
//
//    // ===================================================
//    // When-expression
//
//
//
//
//
//    //=============================================
//    // We need approximator to get this working
//    @Effects("""
//        x == null -> Returns true;
//        x != null -> Returns false
//    """)
//    fun isNull(x: Any?) = x == null
//
//    @Effects("""
//        x != null -> Returns true;
//        x == null -> Returns false
//    """)
//    fun isNotNull(x: Any?) = x != null
//
//    fun test(t: Any?) {
//        val x = assert(isNotNull(t) == isNull(t))
//
//        println("Something")
//    }
//}