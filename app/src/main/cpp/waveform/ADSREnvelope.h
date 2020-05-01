#ifndef BOOPK_ADSRENVELOPE_H
#define BOOPK_ADSRENVELOPE_H

#include "IAmplitudeGenerator.h"

/**
 * Time-based ADSR envelope. Time is measured in frames.
 */
class ADSREnvelope : public IAmplitudeGenerator {
public:

    /**
     * Returns the amplitude for a given frame for Attack, Decay, and Sustain
     * @param frame number
     * @return amplitude at frame
     */
    float getOnPressedAmplitude(int frame) override {
        float amp;
        if (frame <= decayStartFrame) { // Attack
            amp = maxAmplitude * frame / decayStartFrame;
        } else if (frame <= sustainStartFrame) { // Decay
            amp = maxAmplitude + (sustainedAmplitude - maxAmplitude) * (((float)(frame - decayStartFrame)) / ((float)( sustainStartFrame - decayStartFrame)));
        } else { // Sustain
            amp = sustainedAmplitude;
        }
        lastPressedAmplitude.store(amp);
        return amp;
    }

    /**
     * Returns the amplitude for a given frame for Release
     * @param frame number
     * @return amplitude at frame
     */
    float getOnReleaseAmplitude(int frame) override {
        if (frame <= finalReleaseFrame) { // Release
            float releaseAmplitude = lastPressedAmplitude.load();
            return releaseAmplitude - releaseAmplitude * frame / finalReleaseFrame;
        } else { // End
            // Indicates that envelope is done returning values
            return -1;
        }
    }

    /**
     * Sets length of attack in frames
     * @param numFrames - length of attack
     */
    void setAttackLength(int numFrames) {
        sustainStartFrame = sustainStartFrame + numFrames - decayStartFrame;
        decayStartFrame = numFrames;
    }

    /**
     * Sets length of decay in frames
     * @param numFrames - length of decay
     */
    void setDecayLength(int numFrames) {
        sustainStartFrame = numFrames + decayStartFrame;
    }

    /**
     * Sets amplitude to be used after decay finishes
     * @param amplitude - sustained amplitude
     */
    void setSustainedAmplitude(float amplitude) {
        sustainedAmplitude = amplitude;
    }

    /**
     * Sets length of release in frames
     * @param numFrames - length of release
     */
    void setReleaseLength(int numFrames) {
        finalReleaseFrame = numFrames;
    }

private:
    int decayStartFrame    {  50000 };
    int sustainStartFrame  { 100000 };
    float maxAmplitude { 0.69 };
    float sustainedAmplitude { 0.5 };
    int finalReleaseFrame{ 10000 };
    std::atomic<float> lastPressedAmplitude { 0.0 };
};
#endif //BOOPK_ADSRENVELOPE_H
