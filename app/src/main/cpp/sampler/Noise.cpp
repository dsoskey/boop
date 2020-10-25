//
// Created by Daniel Soskey on 7/25/20.
//

#include "Noise.h"

std::array<float,kMaxSamples> Noise::randomNoise() {
    std::array<float,kMaxSamples> noiseData = *(new std::array<float,kMaxSamples>());
    for (int i = 0; i < kMaxSamples; i++) {
        noiseData[i] = ((float) rand()) / (((float) RAND_MAX) * 1.0f) - 1.0f;
    }
    return noiseData;
}