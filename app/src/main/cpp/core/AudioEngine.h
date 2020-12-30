// Based on Megadrone oboe sample:
// https://github.com/google/oboe/blob/master/samples/MegaDrone/src/main/cpp/MegaDroneEngine.h

#ifndef BOOP_CORE_AUDIOENGINE_H
#define BOOP_CORE_AUDIOENGINE_H

#include <oboe/Oboe.h>
#include <vector>
#include "DefaultAudioStreamCallback.h"
#include "Synth.h"
#include "../sampler/WaveSampler.h"
#include "../sampler/DefaultRecordingStreamCallback.h"

/**
 * Oboe-based Audio Engine.
 */
class AudioEngine : public IRestartable {
public:
    AudioEngine(std::vector<int> cpuIds);
    virtual ~AudioEngine() = default;

    // Sampling Interface
    void startRecordingSample(int oscIndex);
    std::vector<float> stopRecordingSample();

    void restart() override;

    std::shared_ptr<Synth> getSynth();

private:
    oboe::ManagedStream stream;
    std::shared_ptr<Synth> audioSource;
    std::unique_ptr<DefaultAudioStreamCallback> callback;
    oboe::Result createPlaybackStream();
    void createCallback(std::vector<int> cpuIds);

    oboe::ManagedStream recordingStream;
    std::shared_ptr<WaveSampler> sampler;
    std::unique_ptr<DefaultRecordingStreamCallback> recordingCallback;
    oboe::Result createRecordingStream(int32_t sampleRate);
    int recordingIndex = -1;

    void start();
};


#endif //BOOP_CORE_AUDIOENGINE_H
