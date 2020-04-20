#ifndef BOOP_WAVEFORM_WAVE_GENERATOR_H
#define BOOP_WAVEFORM_WAVE_GENERATOR_H

#define HIGH_PASS_DEFAULT 100.0

class WaveGenerator {
public:
    virtual float getWaveform(float phase, float amplitude) = 0;
    void setCutoffFrequency(double frequency);
    double getHighPassCutoff();
    double getTimeConstant();
private:
    double timeConstant;// RC
    double highPassCutoff = HIGH_PASS_DEFAULT;
};

#endif //BOOP_WAVEFORM_WAVE_GENERATOR_H
