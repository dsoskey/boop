// Based on Synth from Oboe samples
// https://github.com/google/oboe/blob/master/samples/MegaDrone/src/main/cpp/Synth.h

#ifndef BOOP_CORE_SYNTH_H
#define BOOP_CORE_SYNTH_H

#include <array>

#include "Mixer.h"
#include "MonoToStereo.h"
#include "Oscillator.h"
#include "../waveform/SinWaveformGenerator.h"

constexpr float kOscBaseFrequency = 116.0;
constexpr float kOscDivisor = 33;
constexpr float kOscAmplitude = 0.3;

class Synth : public IRenderableAudio {
public:

    Synth(int32_t sr, int32_t cc) : sampleRate(sr), channelCount(cc) {
        for (int i = 0; i < kMaxTracks; ++i) {
            oscillators[i].setSampleRate(sampleRate);
            oscillators[i].setFrequency(kOscBaseFrequency + (static_cast<float>(i) / kOscDivisor));
            oscillators[i].setAmplitude(kOscAmplitude);
            oscillators[i].setWave(new SinWaveformGenerator());
            oscillators[i].setEnvelope(new ADSREnvelope());
            mixer.addTrack(&oscillators[i]);
        }
        if (channelCount == oboe::ChannelCount::Stereo) {
            outputStage =  &converter;
        } else {
            outputStage = &mixer;
        }
    }

    void setWaveOn(int oscIndex, bool isOn) {
        if (oscIndex >= 0 && oscIndex < oscillators.size()) {
            oscillators[oscIndex].setWaveOn(isOn);
        }
    }

    void setFrequency(int oscIndex, double frequency) {
        if (oscIndex >= 0 && oscIndex < oscillators.size()) {
            oscillators[oscIndex].setFrequency(frequency);
        }
    }

    void setWave(int oscIndex, WaveGenerator* waveGenerator) {
        if (oscIndex >= 0 && oscIndex < oscillators.size()) {
            oscillators[oscIndex].setWave(waveGenerator);
        }
    }

    void setAmplitude(int oscIndex, float amplitude) {
        oscillators[oscIndex].setAmplitude(amplitude);
    }

    // From IRenderableAudio
    void renderAudio(float *audioData, int32_t numFrames) override {
        outputStage->renderAudio(audioData, numFrames);
    };

    void setAttackLength(int millis) {
        for (int i = 0; i < kMaxTracks; ++i) {
            oscillators[i].setAttackLength(millis);
        }
    }

    void setDecayLength(int millis) {
        for (int i = 0; i < kMaxTracks; ++i) {
            oscillators[i].setDecayLength(millis);
        }
    }

    void setSustainedLevel(float amplitude) {
        for (int i = 0; i < kMaxTracks; ++i) {
            oscillators[i].setSustainedLevel(amplitude);
        }
    }

    void setReleaseLength(int millis) {
        for (int i = 0; i < kMaxTracks; ++i) {
            oscillators[i].setReleaseLength(millis);
        }
    }
    virtual ~Synth() {
    }
private:
    // Rendering objects
    std::array<Oscillator, kMaxTracks> oscillators;
    Mixer mixer;
    MonoToStereo converter = MonoToStereo(&mixer);
    IRenderableAudio *outputStage; // This will point to either the mixer or converter, so it needs to be raw
    int32_t sampleRate;
    int32_t channelCount;
};
#endif //BOOP_CORE_SYNTH_H
