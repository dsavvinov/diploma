package system

import system.FALSE
import system.NULL_VAL
import system.TRUE
import system.UNKNOWN


sealed class Premise(var lhs: Expression) {
    abstract fun evaluate(): List<Premise>

    infix fun to(effect: Effect): Assertion {
        return Assertion(this, effect)
    }

    open fun bind(context: Map<Variable, Expression>): Premise {
        lhs = lhs.bind(context)
        return this
    }

    abstract fun not(): Premise

    class NotEqual(lhs: Expression, val rhs: Value) : Premise(lhs) {
        override fun evaluate(): List<Premise> {
            lhs = lhs.evaluate()
            val lhs = lhs
            when(lhs) {
                is Variable -> {
                    // Special case for null-literal
                    if (rhs == NULL_VAL) {
                        if (!lhs.type.name.endsWith("?")) {
                            return listOf(Const(TRUE))
                        }
                    }

                    if (lhs.value == UNKNOWN) {
                        // can't advance evaluation for unknown values
                        return listOf(this)
                    }

                    if (lhs.value != rhs) {
                        return listOf(Const(TRUE))
                    } else {
                        return listOf(Const(FALSE))
                    }
                }

                is EffectSchema -> {
                    return lhs.collectExcept(Effect.Returns(rhs))
                }
            }
            throw IllegalArgumentException("Unknown lhs $lhs")
        }

        override fun toString(): String {
            return "$lhs != $rhs"
        }

        override fun not(): Premise {
            return Equal(lhs, rhs)
        }
    }

    class Equal(lhs: Expression, val rhs: Value) : Premise(lhs) {
        override fun evaluate(): List<Premise> {
            lhs = lhs.evaluate()
            val lhs = lhs
            when(lhs) {
                is Variable -> {
                    // Special case for null-literal
                    if (rhs == NULL_VAL) {
                        if (!lhs.type.name.endsWith("?")) {
                            return listOf(Const(FALSE))
                        }
                    }

                    if (lhs.value == UNKNOWN) {
                        // can't advance evaluation for unknown values
                        return listOf(this)
                    }

                    if (lhs.value == rhs) {
                        return listOf(Const(TRUE))
                    } else {
                        return listOf(Const(FALSE))
                    }
                }

                is EffectSchema -> {
                    return lhs.collectAt(Effect.Returns(rhs))
                }
            }
            throw IllegalArgumentException("Unknown lhs $lhs")
        }

        override fun toString(): String {
            return "$lhs == $rhs"
        }

        override fun not(): Premise {
            return NotEqual(lhs, rhs)
        }
    }

    class Is(lhs: Expression, val rhs: Type) : Premise(lhs) {
        override fun evaluate(): List<Premise> {
            lhs = lhs.evaluate()
            val lhs = lhs
            when (lhs) {
                is Variable -> {
                    // Any? is like UNKNOWN for values, i.e. we can't advance evaluation here
                    if (lhs.type == ANY_NULL)
                        return listOf(this)

                    if (lhs.type == rhs) {
                        return listOf(Const(TRUE))
                    } else {
                        return listOf(Const(FALSE))
                    }
                }
            }
            throw IllegalArgumentException("Unknown lhs $lhs")
        }

        override fun toString(): String {
            return "$lhs is $rhs"
        }

        override fun not(): Premise {
            return NotIs(lhs, rhs)
        }
    }

    class NotIs(lhs: Expression, val rhs: Type) : Premise(lhs) {
        override fun evaluate(): List<Premise> {
            lhs = lhs.evaluate()
            val lhs = lhs
            when (lhs) {
                is Variable -> {
                    // Any? is like UNKNOWN for values, i.e. we can't advance evaluation here
                    if (lhs.type == ANY_NULL)
                        return listOf(this)

                    if (lhs.type != rhs) {
                        return listOf(Const(TRUE))
                    } else {
                        return listOf(Const(FALSE))
                    }
                }
            }
            throw IllegalArgumentException("Unknown lhs $lhs")
        }

        override fun toString(): String {
            return "$lhs not is $rhs"
        }

        override fun not(): Premise {
            return Is(lhs, rhs)
        }
    }

    class Const(val const : Constant) : Premise(const) {
        override fun evaluate(): List<Const> {
            return listOf(this)
        }

        override fun bind(context: Map<Variable, Expression>): Premise {
            return this
        }

        override fun toString(): String {
            return const.toString()
        }

        override fun not(): Premise {
            return Const(const.negate())
        }
    }
}