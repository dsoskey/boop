#include "Oscillator.h"
#include <math.h>

#define TWO_PI (3.14159 * 2)
#define AMPLITUDE 3.0

void Oscillator::setFrequency(int32_t sampleRate, double frequency) {
    frequency_.store(frequency);
    setSampleRate(sampleRate);
}

void Oscillator::setSampleRate(int32_t sampleRate) {
    phaseIncrement_ = (TWO_PI * frequency_.load()) / (double) sampleRate;
}

void Oscillator::setWaveOn(bool isWaveOn) {
    isWaveOn_.store(isWaveOn);
    if (isWaveOn) {
        // start timer
        // store current time
    } else {
        // stop and reset timer
        // clear current time
    }
}

void Oscillator::setWave(WaveGenerator *waveGenerator) {
// TODO: Move cleanup of waveform generators to Synthesizer to avoid a MEMORY LEAK
    //    WaveGenerator* old = this->waveGenerator;
    this->waveGenerator = waveGenerator;
//    delete old;
}

// TODO: How long does numFramesTake. Just start a timer when isWaveOn is
void Oscillator::render(float *audioData, int32_t numFrames) {

    if (!isWaveOn_.load()) phase_ = 0;

    for (int i = 0; i < numFrames; i++) {

        if (isWaveOn_.load()) {
            // get current time
            // timeInMillis = current - starTime
            // AMPLITUTE = ADSR.getAmplitude(time)
            audioData[i] = (float) (waveGenerator->getWaveform(phase_) * AMPLITUDE); //TODO: Get the amplitude from the ADSR here
            // Sin is the arbitrary waveform generation function passed in
            // Could be an abstraction for an array of static values a.k.a. a wavetable
            // WavetableGenerator
            // Cyclical? Modulo division
            phase_ += phaseIncrement_;
            if (phase_ > TWO_PI) phase_ -= TWO_PI;
        } else {
            // Output silence by setting sample to 0
            audioData[i] = 0;
        }
    }
}