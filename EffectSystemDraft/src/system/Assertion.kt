package system

/**
 * Created by dsavvinov on 25.11.16.
 */

data class Assertion(val premise: Premise, val effect: Effect) {
    fun evaluate(): List<Assertion> {
        /** Sometimes, when evaluating complex premises, they can raise a bunch of new premises.
         *  Then we just raise a bunch of new assertions.
         */
        return premise.evaluate().map { Assertion(it, effect) }
    }

    fun bind(context: Map<Variable, Expression>): Assertion {
        premise.bind(context)
        effect.bind(context)

        return this
    }

    override fun toString(): String {
        return "$premise -> $effect"
    }
}