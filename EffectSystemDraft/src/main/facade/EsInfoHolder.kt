package main.facade

import main.structure.general.EsConstant
import main.structure.general.EsType
import main.structure.general.EsVariable

interface EsInfoHolder {
    fun getVariableType(variable: EsVariable): EsType?
    fun getVariableValue(variable: EsVariable): EsConstant?
}