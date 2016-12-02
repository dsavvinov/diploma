package main.system

interface LValue {
    fun equalTo(other: LValue): LValue
}