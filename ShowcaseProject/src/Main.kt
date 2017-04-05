///**
// * Created by dsavvinov on 27.02.17.
// */
//
//@Effects("""
//    true -> Returns 42
//""")
//fun foo() : Int = 42
//
//@Effects("""
//    s == "" -> Returns 0
//""")
//fun bar(s: String, x: Int) : Int {
//    return Integer.parseInt(s)
//}
//
//
//
//
//@Effects("""
//    x == 0 -> Returns 0;
//    x != 0 -> Returns 30
//""")
//fun baz(x: Int): Int = if (x == 0) 0 else 30
//
//@Effects("""
//    x == y && z == "" && w == true -> Returns true;
//    !(x == y && z == "" && w == true) -> Returns false
//""")
//fun test(x: Int, y: Int, z: String, w: Boolean, q: Boolean, p: Int) : Boolean {
//    return x == y && z == "" && w
//}
//
//@Effects("""
//    x is String -> Returns 0;
//    x !is String -> Throws NullPointerException
//""")
//fun assertIsString(x: Any?) : Int {
//    assert(x is String)
//    return 0
//}
//
//object Main {
//    @JvmStatic
//    fun main(args: Array<String>) {
//
//    }
//}