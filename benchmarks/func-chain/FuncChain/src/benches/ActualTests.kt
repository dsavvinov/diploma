package benches

fun testStream(c : List<String>) : Int {
    return c.asSequence()
            .map { it.length }
            .map { it * it }
            .map { it + 42 }
            .map { it / 2 }
            .filter { it % 2 == 0 }
            .count()
}

fun testChain(c : List<String>) : Int {
    return c
            .map { it.length }
            .map { it * it }
            .map { it + 42 }
            .map { it / 2 }
            .filter { it % 2 == 0 }
            .count()
}

fun testLoopFused(c : List<String>) : Int {
    var res = 0
    for (i in c) {
        val length = i.length;
        if ( (length * length + 42) / 2 % 2 == 0) {
            res += 1
        }
    }
    return res
}
