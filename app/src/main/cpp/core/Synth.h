// Based on Synth from Oboe samples
// https://github.com/google/oboe/blob/master/samples/MegaDrone/src/main/cpp/Synth.h

#ifndef BOOP_CORE_SYNTH_H
#define BOOP_CORE_SYNTH_H

#include <array>

#include "Mixer.h"
#include "MonoToStereo.h"
#include "Oscillator.h"
#include "../waveform/SinWaveformGenerator.h"

constexpr float oscBaseFrequency = 116.0;
constexpr float oscDivisor = 33.0;
constexpr float oscAmplitude = 0.3;

/**
 * Top-level Synthesizer object that contains a list of oscillators and a mixer to add them together.
 */
class Synth : public IRenderableAudio {
public:

    /**
     * Constructor
     * @param sr - sample rate
     * @param cc - channel count
     */
    Synth(int32_t sr, int32_t cc) : sampleRate(sr), channelCount(cc) {
        for (int i = 0; i < kMaxTracks; ++i) {
            oscillators[i].setSampleRate(sampleRate);
            oscillators[i].setFrequency(oscBaseFrequency + (static_cast<float>(i) / oscDivisor));
            oscillators[i].setAmplitude(oscAmplitude);
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

    // From IRenderableAudio
    void renderAudio(float *audioData, int32_t numFrames) override {
        outputStage->renderAudio(audioData, numFrames);
    };

    /**
     * Turns an oscillator at oscIndex on or off
     * @param oscIndex
     * @param isOn
     */
    void setWaveOn(int oscIndex, bool isOn) {
        if (oscIndex >= 0 && oscIndex < oscillators.size()) {
            oscillators[oscIndex].setWaveOn(isOn);
        }
    }

    /**
     * Sets base frequency of an oscillator at oscIndex
     * @param oscIndex
     * @param frequency
     */
    void setFrequency(int oscIndex, double frequency) {
        if (oscIndex >= 0 && oscIndex < oscillators.size()) {
            oscillators[oscIndex].setFrequency(frequency);
        }
    }

    /**
     * Sets waveform generator of an oscillator at oscIndex
     * @param oscIndex
     * @param waveGenerator
     */
    void setWave(int oscIndex, WaveGenerator* waveGenerator) {
        if (oscIndex >= 0 && oscIndex < oscillators.size()) {
            oscillators[oscIndex].setWave(waveGenerator);
        }
    }

    /**
     * Set amplitude of an oscillator at oscIndex
     * @param oscIndex
     * @param amplitude
     */
    void setAmplitude(int oscIndex, float amplitude) {
        oscillators[oscIndex].setAmplitude(amplitude);
    }

    /**
     * Set attack length of all oscillators
     * @param millis
     */
    void setAttackLength(int millis) {
        for (int i = 0; i < kMaxTracks; ++i) {
            oscillators[i].setAttackLength(millis);
        }
    }

    /**
     * Set decay length of all oscillators
     * @param millis
     */
    void setDecayLength(int millis) {
        for (int i = 0; i < kMaxTracks; ++i) {
            oscillators[i].setDecayLength(millis);
        }
    }

    /**
     * Set sustatined level of all oscillators
     * @param amplitude
     */
    void setSustainedLevel(float amplitude) {
        for (int i = 0; i < kMaxTracks; ++i) {
            oscillators[i].setSustainedLevel(amplitude);
        }
    }

    /**
     * Set release length of all oscillators
     * @param millis
     */
    void setReleaseLength(int millis) {
        for (int i = 0; i < kMaxTracks; ++i) {
            oscillators[i].setReleaseLength(millis);
        }
    }

    virtual ~Synth() {}
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
