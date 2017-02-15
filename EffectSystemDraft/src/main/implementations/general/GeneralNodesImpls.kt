package main.implementations.general

import main.structure.general.EsConstant
import main.structure.general.EsType
import main.structure.general.EsVariable

data class EsVariableImpl(override val name: String, override val type: EsType) : EsVariable

data class EsConstantImpl(override val type: EsType, override val value: Any?) : EsConstant {
    override fun toString(): String {
        return value.toString()
    }
}