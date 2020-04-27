#ifndef BOOPK_ADSRENVELOPE_H
#define BOOPK_ADSRENVELOPE_H

#include "AmplitudeGenerator.h"

class ADSREnvelope : public AmplitudeGenerator {
public:
    float getOnPressedAmplitude(int frame) override {
        float amp;
        if (frame <= decayStartFrame) {
            amp = maxAmplitude * frame / decayStartFrame;
        } else if (frame <= sustainStartFrame) {
            amp = maxAmplitude + (sustainedAmplitude - maxAmplitude) * (((float)(frame - decayStartFrame)) / ((float)( sustainStartFrame - decayStartFrame)));
        } else {
            amp = sustainedAmplitude;
        }
        lastPressedAmplitude.store(amp);
        return amp;
    }

    float getOnReleaseAmplitude(int frame) override {
        if (frame <= finalReleaseFrame) {
            float releaseAmplitude = lastPressedAmplitude.load();
            return releaseAmplitude - releaseAmplitude * frame / finalReleaseFrame;
        } else {
            // Indicates that envelope is done returning values
            return -1;
        }
    }

    void setAttackLength(int numFrames) {
        sustainStartFrame = sustainStartFrame + numFrames - decayStartFrame;
        decayStartFrame = numFrames;
    }

    void setDecayLength(int numFrames) {
        sustainStartFrame = numFrames + decayStartFrame;
    }

    void setSustainedAmplitude(float amplitude) {
        sustainedAmplitude = amplitude;
    }

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
