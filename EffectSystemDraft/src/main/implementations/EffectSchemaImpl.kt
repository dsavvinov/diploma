package main.implementations

import main.structure.*
import main.structure.Function

class EffectSchemaImpl(override val function: Function, override var effects: List<Effect>)
    : EffectSchema, Collection<Effect> by effects
{
    override fun accept(visitor: Visitor): EffectSchema {
        return visitor.visit(this)
    }
}