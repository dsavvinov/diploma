package system

/**
 * Created by dsavvinov on 25.11.16.
 */

object EffectSystem {
    fun inferEffect(application: Application): EffectSchema {
        return application.evaluate() as EffectSchema
    }
}