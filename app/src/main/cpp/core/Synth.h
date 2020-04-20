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


class Synth : IRenderableAudio {
public:

    Synth(int32_t sr, int32_t cc) : sampleRate(sr), channelCount(cc) {
        for (int i = 0; i < kMaxTracks; ++i) {
            mOscs[i].setSampleRate(sampleRate);
            mOscs[i].setFrequency(kOscBaseFrequency + (static_cast<float>(i) / kOscDivisor));
            mOscs[i].setAmplitude(kOscAmplitude);
            mOscs[i].setWave(new SinWaveformGenerator());
            mMixer.addTrack(&mOscs[i]);
        }
        if (channelCount == oboe::ChannelCount::Stereo) {
            mOutputStage =  &mConverter;
        } else {
            mOutputStage = &mMixer;
        }
    }

    void setWaveOn(int oscIndex, bool isOn) {
        if (oscIndex >= 0 && oscIndex < mOscs.size()) {
            mOscs[oscIndex].setWaveOn(isOn);
        }
    }

    void setFrequency(int oscIndex, double frequency) {
        if (oscIndex >= 0 && oscIndex < mOscs.size()) {
            mOscs[oscIndex].setFrequency(frequency);
        }
    }

    void setWave(int oscIndex, WaveGenerator* waveGenerator) {
        if (oscIndex >= 0 && oscIndex < mOscs.size()) {
            mOscs[oscIndex].setWave(waveGenerator);
        }
    }

    void setAmplitude(int oscIndex, float amplitude) {
        mOscs[oscIndex].setAmplitude(amplitude);
    }

    // From IRenderableAudio
    void renderAudio(float *audioData, int32_t numFrames) override {
        mOutputStage->renderAudio(audioData, numFrames);
    };

    virtual ~Synth() {
    }
private:
    // Rendering objects
    std::array<Oscillator, kMaxTracks> mOscs;
    Mixer mMixer;
    MonoToStereo mConverter = MonoToStereo(&mMixer);
    IRenderableAudio *mOutputStage; // This will point to either the mixer or converter, so it needs to be raw
    int32_t sampleRate;
    int32_t channelCount;
};
#endif //BOOP_CORE_SYNTH_H
