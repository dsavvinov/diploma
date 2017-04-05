///**
// * Created by dsavvinov on 29.03.17.
// */
//
//object DefinitelyInvoked {
//    @Effects("""
//        true -> Calls(body, 1)
//    """)
//    fun <R> myRun(body: () -> R): R = body()
//
//    fun runShowcase() {
//        val a: Int
//
//        run {
//            a = 5
//        }
//    }
//
//
//
//}