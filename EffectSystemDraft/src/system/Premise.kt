package system

import tests.FALSE
import tests.NULL_VAL
import tests.TRUE
import tests.UNKNOWN

/**
 * Created by dsavvinov on 25.11.16.
 */

sealed class Premise(var lhs: Expression) {
    abstract fun evaluate(): List<Premise>
    abstract fun bind(context: Map<Variable, Expression>): Premise

    class NotEqual(lhs: Variable, val rhs: Value) : Premise(lhs) {
        override fun evaluate(): List<Premise> {
            lhs = lhs.evaluate()
            val lhs = lhs
            when(lhs) {
                is Variable -> {
                    // Special case for null-literal
                    if (rhs == NULL_VAL) {
                        if (lhs.type.name.endsWith("?")) {
                            return listOf(Const(FALSE))
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

        override fun bind(context: Map<Variable, Expression>): Premise {
            lhs = lhs.bind(context)
            return this
        }

        override fun toString(): String {
            return "$lhs != $rhs"
        }
    }

    class Equal(lhs: Variable, val rhs: Value) : Premise(lhs) {
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

        override fun bind(context: Map<Variable, Expression>): Premise {
            lhs = lhs.bind(context)
            return this
        }

        override fun toString(): String {
            return "$lhs == $rhs"
        }
    }

    class Is(lhs: Variable, val rhs: Type) : Premise(lhs) {
        override fun evaluate(): List<Premise> {
            lhs = lhs.evaluate()
            val lhs = lhs
            when (lhs) {
                is Variable ->
                    if (lhs.type == rhs) {
                        return listOf(Const(TRUE))
                    } else {
                        return listOf(Const(FALSE))
                    }
            }
            throw IllegalArgumentException("Unknown lhs $lhs")
        }

        override fun bind(context: Map<Variable, Expression>): Premise {
            lhs = lhs.bind(context)
            return this
        }

        override fun toString(): String {
            return "$lhs is $rhs)"
        }

        override fun negationToString(): String {
            return "$lhs is not $rhs"
        }
    }

    class Const(val const : Constant) : Premise(const) {
        override fun evaluate(): List<Const> {
            return listOf(this)
        }

        override fun bind(context: Map<Variable, Expression>): Premise {
            return this
        }

        override fun negationToString(): String {
            return const.negate().toString()
        }

        override fun toString(): String {
            return const.toString()
        }
    }

    class Not(val premise: Premise) : Premise(premise.lhs) {
        override fun evaluate(): List<Premise> {
            val evaluated = premise.evaluate()
            return evaluated.map {
                if (it is Const) {
                    Const(it.const.negate())
                } else {
                    Not(it)
                }
            }
        }

        override fun bind(context: Map<Variable, Expression>): Premise {
            premise.bind(context)
            return this
        }

        override fun negationToString(): String {
            return premise.toString()
        }

        override fun toString(): String {
            return premise.negationToString()
        }
    }

    fun not() : Premise {
        return Not(this)
    }
}