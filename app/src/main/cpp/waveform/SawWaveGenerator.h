#ifndef BOOP_WAVEFORM_SAW_WAVE_GENERATOR_H
#define BOOP_WAVEFORM_SAW_WAVE_GENERATOR_H

#include "WaveGenerator.h"

/**
 * Creates a basic saw wave with the given number of voices.
 * Note - Until caching is done at the WaveGenerator level don't use more than 69 voices.
 */
class SawWaveGenerator: public WaveGenerator {
public:
    SawWaveGenerator(int numVoices) {
        if (numVoices < 1) {
            this->numVoices = 10;
        } else {
            this->numVoices = numVoices;
        }
    };
    float getWaveform(float phase, float amplitude) override {
        float val = 0.0;
        for (int i = 1; i <= this->numVoices; i++) {
            val += sin(i * phase) / i;
        }
        return amplitude * val;
    };

    virtual ~SawWaveGenerator() = default;
private:
    int numVoices;
};


#endif //BOOP_WAVEFORM_SAW_WAVE_GENERATOR_H
