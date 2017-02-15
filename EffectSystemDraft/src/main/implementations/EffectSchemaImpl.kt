package main.implementations

import main.structure.general.EsFunction
import main.structure.general.EsVariable
import main.structure.schema.Effect
import main.structure.schema.EffectSchema


class EffectSchemaImpl(
        override val function: EsFunction,
        override val returnVar: EsVariable,
        override val effects: List<Effect>
) : EffectSchema