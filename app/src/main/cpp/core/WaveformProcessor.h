//
// Created by Daniel Soskey on 6/2/20.
//

#ifndef BOOP_WAVEFORMPROCESSOR_H
#define BOOP_WAVEFORMPROCESSOR_H

#include "ISignalProcessor.h"
#include "../waveform/WaveGenerator.h"
#include <math.h>

class WaveformProcessor : public ISignalProcessor {
public:
    void renderSignal(float *audioData, int32_t numFrames, int burstNum, bool isReleasing) override {
        for (int i = 0; i < numFrames; ++i) {
            audioData[i] *= waveGenerator->getWaveform(phase, 1.0);
            phase += phaseIncrement;
            if (phase > kTwoPi) phase -= kTwoPi;
        }
    }

    /**
     * Sets wave generator component of oscillator
     * @param waveGenerator
     */
    void setWave(std::shared_ptr<WaveGenerator> waveGenerator) {
        this->waveGenerator = waveGenerator;
    }

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
        LOGE("SETTING FREQUENCY!");
        this->frequency = frequency;
        updatePhaseIncrement();
    };

    virtual ~WaveformProcessor() = default;

private:
    std::shared_ptr<WaveGenerator> waveGenerator;

    float phase = 0.0;
    std::atomic<double> phaseIncrement { 0.0 };
    double frequency = kDefaultFrequency;
    int32_t sampleRate = kDefaultSampleRate;

    void updatePhaseIncrement(){
        phaseIncrement.store((kTwoPi * frequency) / static_cast<double>(sampleRate));
    };
};

#endif //BOOP_WAVEFORMPROCESSOR_H
