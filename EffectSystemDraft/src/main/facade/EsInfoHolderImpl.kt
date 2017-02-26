package main.facade

import main.structure.general.EsConstant
import main.structure.general.EsType
import main.structure.general.EsVariable

data class EsInfoHolderImpl(
        val varsValues: Map<EsVariable, EsConstant>,
        val varsTypes: Map<EsVariable, EsType>
) : EsInfoHolder {
    // TODO: We need some kind of iterator over statements about terms

    override fun getVariableType(variable: EsVariable): EsType? {
        return varsTypes[variable]
    }

    override fun getVariableValue(variable: EsVariable): EsConstant? {
        return varsValues[variable]
    }
}