package main.structure.schema.effects

data class EffectsPipelineFlags(
        private var isVetoed: Boolean = false   /** After vetoed return pipeline immediately, resulting in last result **/
) {
    fun veto() { isVetoed = true }
    fun isVetoed() = isVetoed
}