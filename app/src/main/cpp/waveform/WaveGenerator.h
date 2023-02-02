#ifndef BOOP_WAVEFORM_WAVE_GENERATOR_H
#define BOOP_WAVEFORM_WAVE_GENERATOR_H

//#define HIGH_PASS_DEFAULT 100.0

/**
 * (Soon to be) abstract class for components that render waveforms.
 */
class WaveGenerator {
public:
    /**
     * Gets the amplitude of the wave at a given phase and base amplitude
     * IDEA: Should amplitude be passed in or should float output be normalized to [0.0, 1.0]?
     * @param phase
     * @param amplitude
     * @return - amplitude of the sample at that phase
     */
    virtual float getWaveform(float phase, float amplitude) = 0;

// TODO: implement this interface when creating a high/low/dual-pass filter
//    void setCutoffFrequency(double frequency);
//    double getHighPassCutoff();
//    double getTimeConstant();
private:
//    double timeConstant;// RC
//    double highPassCutoff = HIGH_PASS_DEFAULT;
};

#endif //BOOP_WAVEFORM_WAVE_GENERATOR_H

/* IDEA: Add a caching layer to WaveGenerator to help with:
 *  - computationally intensive waveforms
 *  - waveforms loaded from files
 */
