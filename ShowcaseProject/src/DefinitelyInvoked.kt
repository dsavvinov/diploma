/**
 * Created by dsavvinov on 29.03.17.
 */

object DefinitelyInvoked {
    @Effects("""
        true -> Calls(block 1)
    """)
    fun <R> myRun(block: () -> R): R = block()

    @Effects("""
        true -> Calls(block 2)
    """)
    fun <R> invokeTwice(block: () -> R): R = block()

    @Effects("""
        true -> Calls(first 1; second 1; third 1)
    """)
    fun invokeAll(first: () -> Unit, second: () -> Unit, third: () -> Unit) {
        first()
        second()
        third()
    }

    fun definitelyInit() {
        val t: Int
        myRun { t = 10 }
        println(t)
    }

    fun controlFlowSanityCheck() {
        myRun { t = 10 } // EXPECTED ERROR: not declared yet
        val t: Int
        println(t)       // EXPECTED ERROR: not initialized yet
        myRun { t = 10 }
        println(t)       // declared and initialized exactly once
        myRun { t = 10 } // EXPECTED ERROR: reassignment
        println(t)
    }

    fun multiInvoke() {
        val f: Int
        val s: String
        val t: Double

        invokeAll( { f = 10 } , { t = 0.0;  } , { s = "" } )

        println(s)
    }

    fun multiInvokeSanityCheck() {
        val f: Int
        val s: String
        val t: Double

        println(s); println(f); println(t)      // EXPECTED ERROR: non initialzied yet

        invokeAll(
                { f = 10 },
                { t = 0.0; f = 5; },            // EXPECTED ERROR: reassignment
                { s = ""; f = 5; t = 3.0; }     // EXPECTED ERROR: reassignment. Note that repeating reassignment on 'f' is not reported.
        )

        println(s); println(f); println(t)
    }

    fun multiInit() {
        val f: Int
        val s: Int

        myRun {
            f = 5
            s = 10
        }

        println(f + s)
    }

    fun composedInit() {
        val t: Int

        myRun { myRun { t = 10 } }

        println(t)
    }

    @Effects("""
        condition == true -> Calls(thenBlock 1);
        condition == false -> Calls(elseBlock 1)
    """)
    fun functionalIf(condition: Boolean, thenBlock: () -> Unit, elseBlock: () -> Unit) {
        if (condition) thenBlock() else elseBlock()
    }

    fun conditionalInit() {
        val t: Int
        val s: Int

        functionalIf(true, { t = 5 }, { s = 10 } )
        println(t)
        println(s)  // EXPECTED ERROR: not initialized


    }

    fun conditionInit2() {
        val t: Int
        val s: Int

        functionalIf(false, { t = 5 }, { s = 10 } )
        println(t)  // EXPECTED ERROR: not initialized
        println(s)
    }

    fun conditionInit3() {
        val t: Int
        val s: Int

        functionalIf(false, { t = 5 }, { s = 10 } )
        println(s)

        functionalIf(true, { t = 10 }, { s = 5; t = 15; TODO() } )
        println(t)

        functionalIf(true, { t = 10 }, { s = 5 } ) // EXPECTED ERROR: reassignment
    }


    fun nonAnonymous(list: List<Int>) {
        val t: Int

        val block = { t = 10 }  // Can be allowed only for a very special cases

        println(t)      // EXPECTED ERROR: initializing lambda not invoked yet

        myRun(block)

        println(t)      // We *can* try to make it work, but its hard; see below

        // Things like that immensely complicate definite-invoke analysis, so, most probably, we should give up
        list.forEach{ block() }
    }
}