///**
// * Created by dsavvinov on 27.03.17.
// */
//
//
//object Reachability {
//
//    @Effects("""
//        condition == true -> Returns unit;
//        condition != true -> Throws AssertionError
//    """)
//    fun myAssert(condition: Boolean): Unit {
//        if (!condition) throw AssertionError("Assertion failed")
//    }
//
//
//    @Effects("""
//        x == 42 -> Returns true;
//        x != 42 -> Returns false
//    """)
//    fun isEqualTo42(x: Int): Boolean = x == 42
//
//
//    fun unreachableCodeShowcase() {
//        myAssert(isEqualTo42(41))
//
//        println("You shall not pass")
//    }
//}
