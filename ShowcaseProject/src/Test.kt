/**
 * Created by dsavvinov on 28.03.17.
 */

object Test {
    @Effects("""
        true -> Hints(RESULT, Int)
    """)
    fun stupidSignature(): Any = 42

    fun hintsTrivialTest() {
        val s = stupidSignature()
        s.and(5)
    }

    @Effects("""
        true -> Hints(value, (block(value) at Returns(true)) typeOf value)
    """)
    fun blockAssert(value: Any?, block: (x: Any?) -> Boolean) {
        if (!block(value)) throw IllegalArgumentException()
    }

    fun testBlockAssert(y: Any?) {
        blockAssert(y, { x: Any? -> x is String } )
        val x = y.length
    }

    @Effects("""
        true -> Hints(input, List < (predicate(x) at Returns(true)) typeOf x>)
    """)
    fun <T> myFilter(input: Iterable<T>, predicate: (x: T) -> Boolean): List<T>
            = input.filter(predicate)

    fun filterTest(list: List<Any>) {
        myFilter(list) { x -> x is String }
    }

    @Effects("""
        true -> Hints(input, List < (block(x) at Returns(Unknown)) typeOf x>)
    """)
    fun <T> myForEach(input: Iterable<T>, block: (x : T) -> Unit): Unit = input.forEach(block)

    fun forEachTest(list: List<Any>) {
        myForEach(list, { x -> if (x !is String) throw IllegalArgumentException() } )
    }
}

