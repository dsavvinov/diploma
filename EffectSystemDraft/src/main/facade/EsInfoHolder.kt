package main.facade

import main.structure.general.EsConstant
import main.structure.general.EsType
import main.structure.general.EsVariable

/**
 * Another part of interface between compiler and effect system.
 * EsInfoHolder contains some knowledge about context variables and
 * functions, and can respond to compilers questions.
 */
interface EsInfoHolder {
    fun getVariableType(variable: EsVariable): EsType?
    fun getVariableValue(variable: EsVariable): EsConstant?
}