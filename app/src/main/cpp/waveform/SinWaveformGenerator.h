#ifndef BOOP_WAVEFORM_SIN_WAVE_GENERATOR_H
#define BOOP_WAVEFORM_SIN_WAVE_GENERATOR_H

#include "WaveGenerator.h"

class SinWaveformGenerator: public WaveGenerator {
public:
    float getWaveform(float phase, float amplitude) {
        return sin(phase) * amplitude;
    };
};


#endif //BOOP_WAVEFORM_SIN_WAVE_GENERATOR_H
