package benches

fun testStream(c : List<Int>) : Int {
    return c.asSequence()
            .map { if (it < 0) it - (it / 2) else it - (it / 4) }
            .map { it * it }
            .map { it + 42 }
            .map { it / 2 }
            .filter { it % 2 == 0 }
            .count()
}

fun testChain(c : List<Int>) : Int {
    return c
            .map { if (it < 0) it - (it / 2) else it - (it / 4) }
            .map { it * it }
            .map { it + 42 }
            .map { it / 2 }
            .filter { it % 2 == 0 }
            .count()
}

fun testLoopFused(c : List<Int>) : Int {
    var res = 0
    for (i in c) {
        val length = if (i < 0) i - (i / 2) else i - (i / 4)
        if ( (length * length + 42) / 2 % 2 == 0) {
            res += 1
        }
    }
    return res
}

1