package main.system

data class Assertion(var premise: Premise, var effect: Effect) {
    fun evaluate(context: Map<Variable, EffectSchema>): List<Assertion> {
        /** Sometimes, when evaluating complex premises, they can raise a bunch of new premises.
         *  Then we just raise a bunch of new assertions.
         */
        effect = effect.evaluate(context)
        return premise.evaluate(context).map { Assertion(it, effect) }
    }

    override fun toString(): String {
        return "$premise -> $effect"
    }

}