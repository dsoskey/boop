#ifndef BOOP_NOISE_H
#define BOOP_NOISE_H

#include <array>
#include <random>
#include "Sample.h"

namespace Noise {
    /**
     * Generates a sample of random noise to be loaded into a Sample.
     * @return array of generated noise data.
     * TODO: Add max amplitude parameter.
     */
    std::array<float,kMaxSamples> randomNoise();
}

#endif //BOOP_NOISE_H
