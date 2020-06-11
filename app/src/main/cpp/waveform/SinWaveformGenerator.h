#ifndef BOOP_WAVEFORM_SIN_WAVE_GENERATOR_H
#define BOOP_WAVEFORM_SIN_WAVE_GENERATOR_H

#include "WaveGenerator.h"

/**
 * Creates a basic sin wave.
 */
class SinWaveformGenerator: public WaveGenerator {
public:
    float getWaveform(float phase, float amplitude) {
        return sin(phase) * amplitude;
    };

    virtual ~SinWaveformGenerator() = default;
};


#endif //BOOP_WAVEFORM_SIN_WAVE_GENERATOR_H
