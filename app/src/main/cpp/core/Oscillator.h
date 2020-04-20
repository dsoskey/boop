// Based on sample from oboe library
// https://github.com/google/oboe/blob/master/samples/shared/Oscillator.h

#ifndef BOOP_CORE_OSCILLATOR_H
#define BOOP_CORE_OSCILLATOR_H

#include <cstdint>
#include <atomic>
#include <math.h>
#include <memory>
#include "IRenderableAudio.h"

constexpr double kDefaultFrequency = 440.0;
constexpr int32_t kDefaultSampleRate = 48000;
constexpr double kPi = M_PI;
constexpr double kTwoPi = kPi * 2;

class Oscillator : public IRenderableAudio {

public:

    ~Oscillator() = default;

    void setWaveOn(bool isWaveOn) {
        mIsWaveOn.store(isWaveOn);
        /* IDEA one approach to handling ADSR or other time-based signal processing functions
         *
         */
        if (isWaveOn) {
            // start timer
            // store current time
        } else {
            // stop and reset timer
            // clear current time
        }
    };

    void setSampleRate(int32_t sampleRate) {
        mSampleRate = sampleRate;
        updatePhaseIncrement();
    };

    void setFrequency(double frequency) {
        mFrequency = frequency;
        updatePhaseIncrement();
    };

    void setAmplitude(float amplitude) {
        mAmplitude = amplitude;
    };

    // From IRenderableAudio
    // TODO: How long does numFramesTake. Just start a timer when isWaveOn is set to true
    void renderAudio(float *audioData, int32_t numFrames) override {
        // IDEA Code used in original lo/hi-pass filter
        //    double* initialWaveform = NULL;
        //    initialWaveform = new double[numFrames];
        //    double dt = (double) numFrames / (double) sampleRate;
        //    double alpha = waveGenerator->getTimeConstant() / (waveGenerator->getTimeConstant() + dt);

        if (mIsWaveOn){
            for (int i = 0; i < numFrames; ++i) {
//            if (i == 0) {
                audioData[i] = waveGenerator->getWaveform(mPhase, mAmplitude);
//            } else {
//                audioData[i] = (float) (alpha * (audioData[i-1] + initialWaveform[i] - initialWaveform[i-1]));
//                audioData[i] = (float) ((audioData[i-1] + alpha * (initialWaveform[i] - audioData[i-1])) * AMPLITUDE);
//            }
//            audioData[i] = (float) (waveGenerator->getWaveform(phase_) * AMPLITUDE); //TODO: Get the amplitude from the ADSR here
                // Sin is the arbitrary waveform generation function passed in
                // Could be an abstraction for an array of static values a.k.a. a wavetable
                // WavetableGenerator
                // Cyclical? Modulo division
                mPhase += mPhaseIncrement;
                if (mPhase > kTwoPi) mPhase -= kTwoPi;
            }
        } else {
            memset(audioData, 0, sizeof(float) * numFrames);
        }
    };

    void setWave(WaveGenerator* waveGenerator) {
        this->waveGenerator = waveGenerator;
    }


private:
    std::atomic<bool> mIsWaveOn { false };
    float mPhase = 0.0;
    std::atomic<float> mAmplitude { 0 };
    std::atomic<double> mPhaseIncrement { 0.0 };
    double mFrequency = kDefaultFrequency;
    int32_t mSampleRate = kDefaultSampleRate;
    WaveGenerator* waveGenerator;

    void updatePhaseIncrement(){
        mPhaseIncrement.store((kTwoPi * mFrequency) / static_cast<double>(mSampleRate));
    };
};

#endif //BOOP_CORE_OSCILLATOR_H
