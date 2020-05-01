#ifndef BOOP_WAVEFORM_SQUARE_WAVE_GENERATOR_H
#define BOOP_WAVEFORM_SQUARE_WAVE_GENERATOR_H

#include "WaveGenerator.h"

/**
 * Creates a basic
 */
class SquareWaveGenerator: public WaveGenerator {
public:
    float getWaveform(float phase, float amplitude) {
        if (phase <= M_PI) {
            return -amplitude;
        } else {
            return amplitude;
        }
    };
};

#endif //BOOP_WAVEFORM_SQUARE_WAVE_GENERATOR_H
