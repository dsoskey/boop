#ifndef BOOP_WAVEFORM_SAW_WAVE_GENERATOR_H
#define BOOP_WAVEFORM_SAW_WAVE_GENERATOR_H

#include "WaveGenerator.h"

class SawWaveGenerator: public WaveGenerator {
public:
    SawWaveGenerator(int numVoices) {
        if (numVoices < 1) {
            this->numVoices = 10;
        } else {
            this->numVoices = numVoices;
        }
    };
    float getWaveform(float phase, float amplitude) {
        float val = 0.0;
        for (int i = 1; i <= this->numVoices; i++) {
            val += sin(i * phase) / i;
        }
        return amplitude * val;
    };
private:
    int numVoices;
};


#endif //BOOP_WAVEFORM_SAW_WAVE_GENERATOR_H
