// Based on Megadrone oboe sample:
// https://github.com/google/oboe/blob/master/samples/MegaDrone/src/main/cpp/MegaDroneEngine.h

#ifndef BOOP_CORE_AUDIOENGINE_H
#define BOOP_CORE_AUDIOENGINE_H

#include <oboe/Oboe.h>
#include <vector>
#include "DefaultAudioStreamCallback.h"
#include "Synth.h"

/**
 * Oboe-based Audio Engine. TODO: Flatten oscillator out of the AudioEngine
 */
class AudioEngine : public IRestartable {
public:
    AudioEngine(std::vector<int> cpuIds);
    virtual ~AudioEngine() = default;

    // Oscillator Interface
    void setSourceOn(int oscIndex, bool isOn);
    void setFrequency(int oscIndex, double frequency);
    void setWaveform(int oscIndex, WaveGenerator *waveGenerator);
    void setAmplitude(int oscIndex, float amplitude);

    // ADSR Interface
    void setAttackLength(int millis);
    void setDecayLength(int millis);
    void setSustainedLevel(float amplitude);
    void setReleaseLength(int millis);

    virtual void restart() override;

private:
    oboe::ManagedStream stream;
    std::shared_ptr<Synth> audioSource;
    std::unique_ptr<DefaultAudioStreamCallback> callback;

    oboe::Result createPlaybackStream();
    void createCallback(std::vector<int> cpuIds);
    void start();
};


#endif //BOOP_CORE_AUDIOENGINE_H
