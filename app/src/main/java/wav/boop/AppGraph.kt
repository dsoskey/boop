package wav.boop

import android.app.Application
import dagger.Component
import wav.boop.menu.EngineSelectorActionProvider
import wav.boop.synth.DefaultSynthesizer
import javax.inject.Singleton

@Singleton
@Component
interface AppGraph {
    fun synthesizer(): DefaultSynthesizer // TODO: Make it provide a Synthesizer with custom binding
    fun inject(activity: MainActivity)
    fun inject(actionProvider: EngineSelectorActionProvider)
}

class BoopApp: Application() {
    val appGraph = DaggerAppGraph.create()
}