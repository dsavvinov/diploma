package main.implementations

import main.structure.*
import main.structure.Function
import main.util.AnyType

class EffectSchemaImpl(
        override var effects: List<Effect>,
        override val returnVar: Variable = Variable("return", AnyType))
    : EffectSchema, Collection<Effect> by effects
{
    override fun accept(visitor: Visitor): EffectSchema {
        return visitor.visit(this)
    }

    override fun isImplies(op: Operator): Boolean {
        return false
    }
}