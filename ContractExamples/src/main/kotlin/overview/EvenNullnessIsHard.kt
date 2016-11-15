package overview

import java.util.concurrent.locks.Lock
import kotlin.concurrent.*

/**
 * Created by user on 10/8/16.
 */

class NonThreadSafeNullnessOnVars {
    var someField: String? = null

    fun test() {
        var localFiels = someField
        if (localFiels != null) {
        // println(someField.length) // smart cast is impossible because someField could be changed
        /*
           This is reasonable if someField could be accessed by the other thread, and it definitely could,
           so Kotlin does good here.
         */
        }
    }
}

// ok, let's make it thread-safe
class NaiveThreadSafeNullnessOnVars {
    var someField: String? = null

    fun test() {
        /* We could try to do it naively like that:
            synchronized(someField) {
                ...
            }
           But that won't work of course, because we can't synchronize on potential null. But we can't
           test var on null without synchronization! So we have to introduce another value for monitor
        */
    }
}

class ThreadSafeNullnessOnVars {
    var someField: String? = null


    fun test() {
        synchronized(this) {
            if (someField != null) {
//                someField.length
                /*
                   Here is a completely legitimate use of someField, because we **know** that we tested it on nullability
                   and no one can change it back to null after check because we have acquired lock on the object holding
                   this field. Though, Kotlin doesn't understand this.

                   Another situation when compiler could use some help is when we **know** that we won't access this field
                   from another thread. Probably, most of variables never accessed from another thread (at least, people
                   try to design apps in a such way that it is true), so we could say Kotlin is too conservative here.

                   It would be great to have some mechanism to tell the compiler that this field is strictly single-threaded.
                   Of course, compiler shouldn't trust us unconditionally and should perform checks. And it would be even
                   better if compiler could infer such facts on its own!
                 */
            }
        }
    }
}

// but even if Kotlin did understand...

class NullnessAndPurity {
    var someField: String? = null

    fun computeValue(value: Int): Int {
        // ...some complicated computation...
        return value + 1
    }

    fun test() {
        synchronized(this) {
            if (someField != null) {
                computeValue(0)
                // println(someField.length)
                /*
                    Even if we learn how to explain to compiler that it is safe to cast someField to the non-nullable type,
                    we would face problems here, because computeValue() could mess everything, setting someField back to
                    null!
                 */
            }
        }
    }
}

// Java Checker Framework solve this by introducing purity-ness family of annotations (like @Pure), but
// they are too strict - consider if computeValue not really pure, but doesn't touch someField nevertheless

class NullnessAndPurity2 {
    var someField: String? = null
    var computedValue: Int? = null

    fun computeValue(value: Int): Int {
        // ...some complicated computation with "value"...
        computedValue = value + 1
        return value + 1
    }

    fun test() {
        synchronized(this) {
            if (someField != null) {
                computeValue(0)
                // println(someField.length)
            }
        }
    }
}

// In the last example "computeValue" function isn't pure, but it is "pure enough" in a sense that it doesn' change
// some field in any possible way.
// Though, this type of "relative purity" could be very non-trivial (or even not decidable) to prove.

class NullnessAndPurity3 {
    var someField: String? = null
    var computedValue: Int? = null

    // This function sometimes really mess with "someField", and sometimes doesn't.
    fun computeValue(value: Int): Int {
        if (value == 42) {
            someField = null
        }

        // ...some complicated computation with "value"...

        computedValue = value + 1
        return value + 1
    }

    fun test() {
        synchronized(this) {
            if (someField != null) {
                computeValue(0)                            // hypothetically, could be inferred as not modifying someField
                computeValue((readLine() ?: "0").toInt())  // non-decidable at all!
                // println(someField.length)
            }
        }
    }
}

// We can continue on complicating cases. Consider:

class NullnessAndContracts {
    // Following method has contract:
    //  - if arg is null then it throws
    //  - if arg is not null then return length of argument
    // Essentially, side-effect of executing such function is that it *ensures* that argument is not null
    fun lengthOrNothing(str: String?) : Int {
        if (str == null) {
            throw RuntimeException()
        }
        return str.length
    }

    fun foo(maybeStr: String?) {
        val len = lengthOrNothing(maybeStr)
        assert(maybeStr.length == len) // maybeStr is still nullable
        /*
        Compiler doesn't know about contract of `lengthOrNothing()`
         */
    }
    // This can be as complicated as we want - calling to other functions with contracts, more complicated contracts, etc...
}