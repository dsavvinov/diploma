package main.implementations.visitors

import main.lang.*
import main.structure.EffectSchema
import main.structure.Visitor

class Substitutor(val context: MutableMap<Variable, Constant>) : CallTreeVisitor<EffectSchema> {
    override fun visit(call: FunctionCall): EffectSchema {
        val functionEffectSchema = TODO()
        
    }

    override fun visit(isOperator: IsOperator): EffectSchema {

    }

    override fun visit(equalOperator: EqualOperator): EffectSchema {

    }

    override fun visit(variable: Variable): EffectSchema {
    }

    override fun visit(constant: Constant): EffectSchema {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}