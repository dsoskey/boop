// Based on Synth from Oboe samples
// https://github.com/google/oboe/blob/master/samples/MegaDrone/src/main/cpp/Synth.h

#ifndef BOOP_CORE_SYNTH_H
#define BOOP_CORE_SYNTH_H

#include <array>
#include "Mixer.h"
#include "MonoToStereo.h"
#include "Oscillator.h"
#include "../waveform/SinWaveformGenerator.h"
#include "SignalChain.h"
#include "WaveformProcessor.h"
#include "ADSRProcessor.h"

constexpr float oscBaseFrequency = 116.0;
constexpr float oscDivisor = 33.0;
constexpr float oscAmplitude = 0.3;
constexpr unsigned int WAVEFORM_INDEX = 0;
constexpr unsigned int ADSR_INDEX = 1;

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
        std::shared_ptr<WaveGenerator> waveformGenerator = std::make_shared<SinWaveformGenerator>();
        std::shared_ptr<ADSREnvelope> adsrEnvelope = std::make_shared<ADSREnvelope>();
        for (int i = 0; i < kMaxTracks; ++i) {
            std::shared_ptr<WaveformProcessor> waveformProcessor = std::make_shared<WaveformProcessor>();
            waveformProcessor->setFrequency(oscBaseFrequency + (static_cast<float>(i) / oscDivisor));
            waveformProcessor->setSampleRate(sampleRate);
            waveformProcessor->setWave(waveformGenerator);

            std::shared_ptr<ADSRProcessor> adsrProcessor = std::make_shared<ADSRProcessor>();
            adsrProcessor->setEnvelope(adsrEnvelope);

            oscillators[i].addRenderable(waveformProcessor);
            oscillators[i].addRenderable(adsrProcessor);
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
        }
    }

    /**
     * Sets base frequency of an oscillator at oscIndex
     * @param oscIndex
     * @param frequency
     */
    void setFrequency(int oscIndex, double frequency) {
        if (oscIndex >= 0 && oscIndex < oscillators.size()) {
            std::shared_ptr<WaveformProcessor> processor = std::dynamic_pointer_cast<WaveformProcessor>(oscillators[oscIndex].getRenderable(WAVEFORM_INDEX));
            processor->setFrequency(frequency);
        }
    }

    /**
     * Sets waveform generator of an oscillator at oscIndex
     * @param oscIndex
     * @param waveGenerator
     */
    void setWave(int oscIndex, std::shared_ptr<WaveGenerator> waveGenerator) {
        if (oscIndex >= 0 && oscIndex < oscillators.size()) {
            std::dynamic_pointer_cast<WaveformProcessor>(oscillators[oscIndex].getRenderable(WAVEFORM_INDEX))->setWave(waveGenerator);
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
        int frames = millis * (sampleRate / 1000);
        for (int i = 0; i < kMaxTracks; ++i) {
            std::dynamic_pointer_cast<ADSRProcessor>(oscillators[i].getRenderable(ADSR_INDEX))->setAttackLength(frames);
        }
    }

    /**
     * Set decay length of all oscillators
     * @param millis
     */
    void setDecayLength(int millis) {
        int frames = millis * (sampleRate / 1000);
        for (int i = 0; i < kMaxTracks; ++i) {
            std::dynamic_pointer_cast<ADSRProcessor>(oscillators[i].getRenderable(ADSR_INDEX))->setDecayLength(frames);
        }
    }

    /**
     * Set sustatined level of all oscillators
     * @param amplitude
     */
    void setSustainedLevel(float amplitude) {
        for (int i = 0; i < kMaxTracks; ++i) {
            std::dynamic_pointer_cast<ADSRProcessor>(oscillators[i].getRenderable(ADSR_INDEX))->setSustainedLevel(amplitude);
        }
    }

    /**
     * Set release length of all oscillators
     * @param millis
     */
    void setReleaseLength(int millis) {
        int frames = millis * (sampleRate / 1000);
        for (int i = 0; i < kMaxTracks; ++i) {
            std::dynamic_pointer_cast<ADSRProcessor>(oscillators[i].getRenderable(ADSR_INDEX))->setReleaseLength(frames);
        }
    }

    virtual ~Synth() {}
private:
    // Rendering objects
    std::array<SignalChain, kMaxTracks> oscillators;
    Mixer mixer;
    MonoToStereo converter = MonoToStereo(&mixer);
    IRenderableAudio *outputStage; // This will point to either the mixer or converter, so it needs to be raw
    int32_t sampleRate;
    int32_t channelCount;
};
#endif //BOOP_CORE_SYNTH_H
