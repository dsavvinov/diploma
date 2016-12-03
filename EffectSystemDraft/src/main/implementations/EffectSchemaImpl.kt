package main.implementations

import main.structure.*

class EffectSchemaImpl(override var effects: List<Effect>)
    : EffectSchema, Collection<Effect> by effects
{
    override fun accept(visitor: Visitor): EffectSchema {
        return visitor.visit(this)
    }
}