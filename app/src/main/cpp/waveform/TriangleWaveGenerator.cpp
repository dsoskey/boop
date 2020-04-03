#include <math.h>
#include "TriangleWaveGenerator.h"

TriangleWaveGenerator::TriangleWaveGenerator(int numVoices) {
    if (numVoices < 1) {
        this->numVoices = 10;
    } else {
        this->numVoices = numVoices;
    }
}

double TriangleWaveGenerator::getWaveform(double phase) {
    double amplitude = 0.0;
    for (int i = 1; i <= this->numVoices; i++) {
//        amplitude += sin((2.0 * (double) i - 1.0) * phase) * pow(-1.0, ((double) i) / pow((2.0 * (double) i - 1.0), 2.0));
        amplitude += sin((2.0 * (double) i) * phase) * pow(-1.0, ((double) i) / pow((2.0 * (double) i), 2.0));
    }
    return amplitude;
}