// Based on Synth from Oboe samples
// https://github.com/google/oboe/blob/master/samples/MegaDrone/src/main/cpp/Synth.h

#ifndef BOOP_CORE_SYNTH_H
#define BOOP_CORE_SYNTH_H

#include <array>
#include <random>
#include "Mixer.h"
#include "MonoToStereo.h"
#include "Oscillator.h"
#include "../waveform/SinWaveformGenerator.h"
#include "SignalChain.h"
#include "WaveformProcessor.h"
#include "ADSRProcessor.h"
#include "../sampler/Sample.h"
#include "../sampler/Noise.h"

constexpr float oscBaseFrequency = 116.0;
constexpr float oscDivisor = 33.0;
constexpr float oscAmplitude = 0.3;

constexpr int minSamplerIndex = 0;
constexpr int maxSamplerIndex = 7;
constexpr int minPlayPadIndex = 8;
constexpr int maxPlayPadIndex = 39;
/**
 * Top-level Synthesizer object that contains a list of oscillators and a mixer to add them together.
 */
class Synth : public IRenderableAudio {
public:

    /**
     * Signal chain allocation within boop.
     * [0,7] - Sampler pads
     * [8,39] - 4x4 pads
     * [40,41] - Test pad
     *
     * Constructor
     * @param sr - sample rate
     * @param cc - channel count
     */
    Synth(int32_t sr, int32_t cc) : sampleRate(sr), channelCount(cc) {
        std::shared_ptr<WaveGenerator> waveformGenerator = std::make_shared<SinWaveformGenerator>();
        std::shared_ptr<ADSREnvelope> adsrEnvelope = std::make_shared<ADSREnvelope>();
        for (int i = minSamplerIndex; i <= maxSamplerIndex; ++i) {
            std::shared_ptr<Sample> noise = std::make_shared<Sample>(Noise::randomNoise());
            sampleIndex = oscillators[i].addRenderable(noise);
            oscillators[i].setAmplitude(.6);
            mixer.addTrack(&oscillators[i]);
        }

        for (int i = minPlayPadIndex; i < kMaxTracks; ++i) {
            std::shared_ptr<WaveformProcessor> waveformProcessor = std::make_shared<WaveformProcessor>();
            waveformProcessor->setFrequency(oscBaseFrequency + (static_cast<float>(i) / oscDivisor));
            waveformProcessor->setSampleRate(sampleRate);
            waveformProcessor->setWave(waveformGenerator);

            std::shared_ptr<ADSRProcessor> adsrProcessor = std::make_shared<ADSRProcessor>();
            adsrProcessor->setEnvelope(adsrEnvelope);

            waveformIndex = oscillators[i].addRenderable(waveformProcessor);
            adsrIndex = oscillators[i].addRenderable(adsrProcessor);
            oscillators[i].setAmplitude(oscAmplitude);
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
    void setWaveOn(unsigned int oscIndex, bool isOn) {
        if (oscIndex >= 0 && oscIndex < oscillators.size()) {
            oscillators[oscIndex].setOn(isOn);
        } else {
            LOGE("Index out of bounds! Expected range [0, %d], received (%d)", (int)oscillators.size(), oscIndex);
        }
    }

    // TODO: all of these set functions need to check for index bounds of sections of boop, as defined above
    /**
     * Sets base frequency of an oscillator at oscIndex
     * @param oscIndex
     * @param frequency
     */
    void setFrequency(int oscIndex, double frequency) {
        if (oscIndex >= minPlayPadIndex && oscIndex < oscillators.size()) { // TODO: Add upper bound when something is above play pads
            std::shared_ptr<WaveformProcessor> waveProcessor = std::dynamic_pointer_cast<WaveformProcessor>(oscillators[oscIndex].getRenderable(waveformIndex));
            waveProcessor->setFrequency(frequency);
            waveProcessor->setSignalOn(true);
        } else {
            LOGE("Play pad index out of bounds");
        }
    }

    /**
     * Sets waveform generator of an oscillator at oscIndex
     * @param oscIndex
     * @param waveGenerator
     */
    void setWave(int oscIndex, std::shared_ptr<WaveGenerator> waveGenerator) {
        if (oscIndex >= minPlayPadIndex && oscIndex < oscillators.size()) { // TODO: Add upper bound when something is above play pads
            std::shared_ptr<WaveformProcessor> waveProcessor = std::dynamic_pointer_cast<WaveformProcessor>(oscillators[oscIndex].getRenderable(waveformIndex));
            waveProcessor->setWave(waveGenerator);
            waveProcessor->setSignalOn(true);
        } else {
            LOGE("Play pad index out of bounds");
        }
    }

    void setSample(int oscIndex, std::vector<float> data) {
        if (oscIndex >= minSamplerIndex && oscIndex <= maxSamplerIndex) {
            std::shared_ptr<Sample> sampleProcessor = std::dynamic_pointer_cast<Sample>(oscillators[oscIndex].getRenderable(sampleIndex));
            sampleProcessor->setData(data);
            sampleProcessor->setSignalOn(true);
        } else {
            LOGE("Sampler index out of bounds");
        }
    }

    void setSample(int oscIndex, std::array<float, kMaxSamples> data) {
        if (oscIndex >= minSamplerIndex && oscIndex <= maxSamplerIndex) {
            std::shared_ptr<Sample> sampleProcessor = std::dynamic_pointer_cast<Sample>(oscillators[oscIndex].getRenderable(sampleIndex));
            sampleProcessor->setData(data);
            sampleProcessor->setSignalOn(true);
        } else {
            LOGE("Sampler index out of bounds");
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
     * TODO: Make this set an individual oscillator.
     * Set attack length of all oscillators
     * Note: This only applies to the pad play oscillators, NOT the sampler
     * @param millis
     */
    void setAttackLength(int millis) {
        int frames = millis * (sampleRate / 1000);
        for (int i = minPlayPadIndex; i < kMaxTracks; ++i) {
            std::dynamic_pointer_cast<ADSRProcessor>(oscillators[i].getRenderable(adsrIndex))->setAttackLength(frames);
        }
    }

    /**
     * TODO: Make this set an individual oscillator.
     * Set decay length of all oscillators
     * Note: This only applies to the pad play oscillators, NOT the sampler
     * @param millis
     */
    void setDecayLength(int millis) {
        int frames = millis * (sampleRate / 1000);
        for (int i = minPlayPadIndex; i < kMaxTracks; ++i) {
            std::dynamic_pointer_cast<ADSRProcessor>(oscillators[i].getRenderable(adsrIndex))->setDecayLength(frames);
        }
    }

    /**
     * TODO: Make this set an individual oscillator.
     * Set sustained level of all oscillators
     * Note: This only applies to the pad play oscillators, NOT the sampler
     * @param amplitude
     */
    void setSustainedLevel(float amplitude) {
        for (int i = minPlayPadIndex; i < kMaxTracks; ++i) {
            std::dynamic_pointer_cast<ADSRProcessor>(oscillators[i].getRenderable(adsrIndex))->setSustainedLevel(amplitude);
        }
    }

    /**
     * TODO: Make this set an individual oscillator.
     * Set release length of all oscillators
     * Note: This only applies to the pad play oscillators, NOT the sampler
     * @param millis
     */
    void setReleaseLength(int millis) {
        int frames = millis * (sampleRate / 1000);
        for (int i = minPlayPadIndex; i < kMaxTracks; ++i) {
            std::dynamic_pointer_cast<ADSRProcessor>(oscillators[i].getRenderable(adsrIndex))->setReleaseLength(frames);
        }
    }

    virtual ~Synth() {}
private:
    // Rendering objects
    uint8_t waveformIndex; // Only used for play pads
    uint8_t adsrIndex; // Only used for play pads
    uint8_t sampleIndex; // Only used for sampler
    std::array<SignalChain, kMaxTracks> oscillators;
    Mixer mixer;
    MonoToStereo converter = MonoToStereo(&mixer);
    IRenderableAudio *outputStage; // This will point to either the mixer or converter, so it needs to be raw
    int32_t sampleRate;
    int32_t channelCount;
};

#endif //BOOP_CORE_SYNTH_H
