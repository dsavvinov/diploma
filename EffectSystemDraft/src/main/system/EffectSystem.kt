package main.system

object EffectSystem {
    fun inferEffect(application: Application): EffectSchema {
        return application.evaluate()
    }
}