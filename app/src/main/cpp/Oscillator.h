#include <atomic>
#include <stdint.h>
#include <math.h>
#include "waveform/WaveGenerator.h"
#include "waveform/SinWaveformGenerator.h"

#ifndef WAVEMAKER_OSCILLATOR_H
#define WAVEMAKER_OSCILLATOR_H

#define FREQUENCY 440.0

class AudioEngine;
typedef double (AudioEngine::*getWaveFunc)(double phase);
class Oscillator {
public:
    void setWaveOn(bool isWaveOn);
    void setFrequency(int32_t sampleRate, double frequency);
    void setSampleRate(int32_t sampleRate);
    void setWave(WaveGenerator* waveGenerator);
    void render(float *audioData, int32_t numFrames);

private:
    std::atomic<bool> isWaveOn_{false};
    std::atomic<double> frequency_{FREQUENCY};
    WaveGenerator* waveGenerator;
    double phase_ = 0.0;
    double phaseIncrement_ = 0.0;
};

#endif //WAVEMAKER_OSCILLATOR_H
