package main.system

sealed class Effect {
    abstract fun evaluate(context: Map<Variable, Expression>): Effect

    data class Returns(val value: Value) : Effect() {
        override fun toString(): String {
            return "Returns $value"
        }

        override fun evaluate(context: Map<Variable, Expression>): Effect {
            return this
        }
    }
    data class Throws(val value: Exception) : Effect() {
        override fun toString(): String {
            return "Throws ${value.name}"
        }

        override fun evaluate(context: Map<Variable, Expression>): Effect {
            return this
        }
    }

    data class Hints(var ident: Variable, val type: Type) : Effect() {
        override fun toString(): String {
            return "Hints ${ident.name} is ${type.name}"
        }

        override fun evaluate(context: Map<Variable, Expression>): Effect {
            val binder = context[ident] ?: throw IllegalArgumentException("Error binding Effect '${this}': " +
                    "identifier $ident is not found in context")
            if (binder !is Variable) {
                throw IllegalStateException("Error: can't evaulate till 'Variable' type when binding 'Hints'-expression")
            }
            return Hints(binder, type)
        }
    }
}
