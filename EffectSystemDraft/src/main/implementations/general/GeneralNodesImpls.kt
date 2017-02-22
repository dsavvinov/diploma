package main.implementations.general

import main.implementations.ClauseImpl
import main.implementations.EffectSchemaImpl
import main.structure.general.EsConstant
import main.structure.general.EsType
import main.structure.general.EsVariable
import main.structure.lift
import main.structure.schema.EffectSchema
import main.structure.schema.effects.Returns

data class EsVariableImpl(override val name: String, override val type: EsType) : EsVariable {
    override fun castToSchema(): EffectSchema = EffectSchemaImpl(listOf(ClauseImpl(true.lift(), Returns(this, type))))
}

data class EsConstantImpl(override val type: EsType, override val value: Any?) : EsConstant {
    override fun toString(): String {
        return value.toString()
    }

    override fun castToSchema(): EffectSchema = EffectSchemaImpl(listOf(ClauseImpl(true.lift(), Returns(this, type))))
}