package main.implementations

import main.structure.general.EsFunction
import main.structure.general.EsVariable
import main.structure.schema.Clause
import main.structure.schema.EffectSchema


class EffectSchemaImpl(
        override val function: EsFunction,
        override val returnVar: EsVariable,
        override val clauses: List<Clause>
) : EffectSchema