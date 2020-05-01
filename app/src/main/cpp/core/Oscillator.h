// Based on sample from oboe library
// https://github.com/google/oboe/blob/master/samples/shared/Oscillator.h

#ifndef BOOP_CORE_OSCILLATOR_H
#define BOOP_CORE_OSCILLATOR_H

#include <cstdint>
#include <atomic>
#include <math.h>
#include <memory>
#include "IRenderableAudio.h"
#include "../waveform/WaveGenerator.h"
#include "../waveform/ADSREnvelope.h"

constexpr double kDefaultFrequency = 440.0;
constexpr int32_t kDefaultSampleRate = 48000;
constexpr double kTwoPi = M_PI * 2;

class Oscillator : public IRenderableAudio {

public:

    // From IRenderableAudio
    // TODO: How long does numFramesTake. Just start a timer when isWaveOn is set to true
    void renderAudio(float *audioData, int32_t numFrames) override {
        // IDEA Code used in original lo/hi-pass filter
        //    double* initialWaveform = NULL;
        //    initialWaveform = new double[numFrames];
        //    double dt = (double) numFrames / (double) sampleRate;
        //    double alpha = waveGenerator->getTimeConstant() / (waveGenerator->getTimeConstant() + dt);

        if (isWaveOn){
            for (int i = 0; i < numFrames; ++i) {
                float currentAmplitude = amplitude.load();
                if (isWaveReleasing.load()) {
                    currentAmplitude *= adsrEnvelope->getOnReleaseAmplitude(i + numFrames * currentBurst.load());
                } else {
                    currentAmplitude *= adsrEnvelope->getOnPressedAmplitude(i + numFrames * currentBurst.load());
                }
                if (currentAmplitude < 0) {
                    isWaveOn.store(false);
                    audioData[i] = 0;
                } else {
                    audioData[i] = waveGenerator->getWaveform(phase, currentAmplitude);
                }
//            if (i == 0) {
//            } else {
//                audioData[i] = (float) (alpha * (audioData[i-1] + initialWaveform[i] - initialWaveform[i-1]));
//                audioData[i] = (float) ((audioData[i-1] + alpha * (initialWaveform[i] - audioData[i-1])) * AMPLITUDE);
//            }
                phase += phaseIncrement;
                if (phase > kTwoPi) phase -= kTwoPi;
            }
            currentBurst.store(currentBurst.load() + 1);
        } else {
            memset(audioData, 0, sizeof(float) * numFrames);
        }
    };

    /**
     * Sets oscillator on or off and state of ADSR
     * @param isWaveOn
     */
    void setWaveOn(bool isWaveOn) {
        currentBurst.store(0);
        if (isWaveOn) {
            this->isWaveOn.store(true);
            isWaveReleasing.store(false);
        } else {
            isWaveReleasing.store(true);
        }
    };

    /**
     * Sets sample rate of oscillator
     * @param sampleRate
     */
    void setSampleRate(int32_t sampleRate) {
        this->sampleRate = sampleRate;
        updatePhaseIncrement();
    };

    /**
     * Sets frequency of oscillator
     * @param frequency
     */
    void setFrequency(double frequency) {
        this->frequency = frequency;
        updatePhaseIncrement();
    };

    /**
     * Sets wave generator component of oscillator
     * @param waveGenerator
     */
    void setWave(WaveGenerator* waveGenerator) {
        this->waveGenerator = waveGenerator;
    }

    /**
     * Sets ADSR envelope component on oscillator
     * @param generator
     */
    void setEnvelope(ADSREnvelope* generator) {
        adsrEnvelope = generator;
    }

    /**
     * Sets base amplitude of wave being generated
     * @param amplitude
     */
    void setAmplitude(float amplitude) {
        this->amplitude = amplitude;
    };

    /**
     * Sets attack length of ADSR
     * @param numMillis
     */
    void setAttackLength(int numMillis) {
        int frames = numMillis * (sampleRate / 1000);
        this->adsrEnvelope->setAttackLength(frames);
    }

    /**
     * Sets decay length of ADSR
     * @param numMillis
     */
    void setDecayLength(int numMillis) {
        int frames = numMillis * (sampleRate / 1000);
        this->adsrEnvelope->setDecayLength(frames);
    }

    /**
     * Sets sustain amplitude of ADSR
     * @param amplitude
     */
    void setSustainedLevel(float amplitude) {
        this->adsrEnvelope->setSustainedAmplitude(amplitude);
    }

    /**
     * Sets release length of ADSR
     * @param numMillis
     */
    void setReleaseLength(int numMillis) {
        int frames = numMillis * (sampleRate / 1000);
        this->adsrEnvelope->setReleaseLength(frames);
    }

    ~Oscillator() = default;

private:
    std::atomic<bool> isWaveOn { false };
    float phase = 0.0;
    std::atomic<float> amplitude { 0 };
    std::atomic<double> phaseIncrement { 0.0 };
    double frequency = kDefaultFrequency;
    int32_t sampleRate = kDefaultSampleRate;
    WaveGenerator* waveGenerator;
    ADSREnvelope* adsrEnvelope;
    std::atomic<int> currentBurst { 0 };
    std::atomic<bool> isWaveReleasing { false };

    void updatePhaseIncrement(){
        phaseIncrement.store((kTwoPi * frequency) / static_cast<double>(sampleRate));
    };
};

#endif //BOOP_CORE_OSCILLATOR_H
