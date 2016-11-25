package system

/**
 * Created by dsavvinov on 25.11.16.
 */
sealed class Effect {
    data class Returns(val value: Value) : Effect() {
        override fun toString(): String {
            return "Returns $value"
        }

        override fun bind(context: Map<Variable, Expression>) { }
    }
    data class Throws(val value: Exception) : Effect() {
        override fun toString(): String {
            return "Throws ${value.name}"
        }

        override fun bind(context: Map<Variable, Expression>) { }
    }

    data class Hints(var ident: Variable, val type: Type) : Effect() {
        override fun toString(): String {
            return "Hints ${ident.name} is ${type.name}"
        }

        override fun bind(context: Map<Variable, Expression>) {
            val binder = context[ident] ?: throw IllegalArgumentException("Error binding Effect '${this}': " +
                    "identifier $ident is not found in context")
            if (binder !is Variable) {
                throw IllegalStateException("Error: can't evaulate till 'Variable' type when binding 'Hints'-expression")
            }
            ident = binder
        }
    }

//    data class Calls(val application: Application) : Effect()

    abstract fun bind(context: Map<Variable, Expression>)
}
