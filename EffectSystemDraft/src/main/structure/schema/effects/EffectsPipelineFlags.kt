package main.structure.schema.effects

/**
 * Utility class that holds state of the effects-combining pipeline.

 * This is needed for combining Effects with complex rules,
 * such as Outcome, for example, when we have to finish pipeline
 * abruptly when encounter unsuccessful Outcome on the left-side.
 */
data class EffectsPipelineFlags(
        private var isVetoed: Boolean = false   /** After vetoed, return pipeline immediately, resulting in last result **/
) {
    fun veto() { isVetoed = true }
    fun isVetoed() = isVetoed
}