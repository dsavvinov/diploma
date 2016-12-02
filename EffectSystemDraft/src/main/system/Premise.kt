package main.system

import main.system.FALSE
import main.system.NULL_VAL
import main.system.TRUE
import main.system.UNKNOWN
import main.system.Effect.*


sealed class Premise(var lhs: EffectSchema) {
    abstract fun evaluate(context: Map<Variable, EffectSchema>): List<Premise>
    abstract fun implies(otherPremise: Premise): Boolean

    infix fun to(effect: Effect): Assertion {
        return Assertion(this, effect)
    }

    abstract fun not(): Premise

    class NotEqual(lhs: EffectSchema, var rhs: EffectSchema) : Premise(lhs) {
        override fun implies(otherPremise: Premise): Boolean {
            return otherPremise is NotEqual && lhs == otherPremise.lhs && rhs == otherPremise.rhs
        }

        override fun evaluate(context: Map<Variable, EffectSchema>): List<Premise> {
            val lhsES = lhs.evaluate(context)
            val rhsES = rhs.evaluate(context)
            lhs = lhsES
            rhs = rhsES

            val equalSchema = lhsES.equalTo(rhsES)
            return equalSchema.collectAt { it is Returns && it.value == FALSE_VAL }
//            when(lhs) {
//                is Variable -> {
//                    // Special case for null-literal
//                    if (rhs == NULL_VAL) {
//                        if (!lhs.type.name.endsWith("?")) {
//                            return listOf(Const(TRUE))
//                        }
//                    }
//
//                    if (lhs.value == UNKNOWN) {
//                        // can't advance evaluation for unknown values
//                        return listOf(this)
//                    }
//
//                    if (lhs.value != rhs) {
//                        return listOf(Const(TRUE))
//                    } else {
//                        return listOf(Const(FALSE))
//                    }
//                }
//
//                is EffectSchema -> {
//                    return lhs.collectExcept(Effect.Returns(rhs))
//                }
//            }
//            throw IllegalArgumentException("Unknown lhs $lhs")
        }

        override fun toString(): String {
            return "$lhs != $rhs"
        }

        override fun not(): Premise {
            return Equal(lhs, rhs)
        }
    }

    class Equal(lhs: EffectSchema, var rhs: EffectSchema) : Premise(lhs) {
        override fun implies(otherPremise: Premise): Boolean {
            return otherPremise is Equal && lhs == otherPremise.lhs && rhs == otherPremise.rhs
        }

        override fun evaluate(context: Map<Variable, EffectSchema>): List<Premise> {
            val lhsES: EffectSchema = lhs.evaluate(context)
            val rhsES: EffectSchema = rhs.evaluate(context)

            lhs = lhsES
            rhs = rhsES

            val equalES = lhsES.equalTo(rhsES)
            return equalES.collectAt{ it is Returns && it.value == TRUE_VAL }

//            when(lhs) {
//                is Variable -> {
//                    // Special case for null-literal
//                    if (rhs == NULL_VAL) {
//                        if (!lhs.type.name.endsWith("?")) {
//                            return listOf(Const(FALSE))
//                        }
//                    }
//
//                    if (lhs.value == UNKNOWN) {
//                        // can't advance evaluation for unknown values
//                        return listOf(this)
//                    }
//
//                    if (lhs.value == rhs) {
//                        return listOf(Const(TRUE))
//                    } else {
//                        return listOf(Const(FALSE))
//                    }
//                }
//
//                is EffectSchema -> {
//                    return lhs.collectAt(Effect.Returns(rhs))
//                }
//            }
//            throw IllegalArgumentException("Unknown lhs $lhs")
        }

        override fun toString(): String {
            return "$lhs == $rhs"
        }

        override fun not(): Premise {
            return NotEqual(lhs, rhs)
        }
    }

    class Is(lhs: EffectSchema, var rhs: Type) : Premise(lhs) {
        override fun implies(otherPremise: Premise): Boolean {
            return otherPremise is Is && lhs == otherPremise.lhs && rhs.isSubtypeOf(rhs)
        }

        override fun evaluate(context: Map<Variable, EffectSchema>): List<Premise> {
            val lhsES = lhs.evaluate(context)
            lhs = lhsES

            return lhsES.collectAt{
                it is Effect.Hints
                        && it.ident == lhsES.returnVar
                        && it.type.isSubtypeOf(rhs)
            }
//            when (lhs) {
//                is Variable -> {
//                    // Any? is like UNKNOWN for values, i.e. we can't advance evaluation here
//                    if (lhs.type == ANY_NULL)
//                        return listOf(this)
//
//                    if (lhs.type == rhs) {
//                        return listOf(Const(TRUE))
//                    } else {
//                        return listOf(Const(FALSE))
//                    }
//                }
//            }
//            throw IllegalArgumentException("Unknown lhs $lhs")
        }

        override fun toString(): String {
            return "$lhs is $rhs"
        }

        override fun not(): Premise {
            return NotIs(lhs, rhs)
        }
    }

    class NotIs(lhs: EffectSchema, val rhs: Type) : Premise(lhs) {
        override fun implies(otherPremise: Premise): Boolean {
            return otherPremise is Is && lhs == otherPremise.lhs && rhs.isSubtypeOf(rhs)
        }

        override fun evaluate(context: Map<Variable, EffectSchema>): List<Premise> {
            val lhsES = lhs.evaluate(context)
            lhs = lhsES

            return lhsES.collectAt {
                it is Effect.Hints
                        && it.ident == lhsES.returnVar
                        && it.type.isSubtypeOf(rhs).not()
            }

//            when (lhs) {
//                is Variable -> {
//                    // Any? is like UNKNOWN for values, i.e. we can't advance evaluation here
//                    if (lhs.type == ANY_NULL)
//                        return listOf(this)
//
//                    if (lhs.type != rhs) {
//                        return listOf(Const(TRUE))
//                    } else {
//                        return listOf(Const(FALSE))
//                    }
//                }
//            }
//            throw IllegalArgumentException("Unknown lhs $lhs")
        }

        override fun toString(): String {
            return "$lhs not is $rhs"
        }

        override fun not(): Premise {
            return Is(lhs, rhs)
        }
    }

    class Const(val const : Constant) : Premise(const) {
        override fun implies(otherPremise: Premise): Boolean {
            return otherPremise is Const && const == otherPremise.const
        }

        override fun evaluate(context: Map<Variable, EffectSchema>): List<Const> {
            return listOf(this)
        }

        override fun toString(): String {
            return const.toString()
        }

        override fun not(): Premise {
            return Const(const.negate())
        }
    }
}