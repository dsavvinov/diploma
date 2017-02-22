package main.implementations

import main.structure.schema.Clause
import main.structure.schema.EffectSchema


class EffectSchemaImpl(override val clauses: List<Clause>) : EffectSchema