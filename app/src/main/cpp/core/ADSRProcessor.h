//
// Created by Daniel Soskey on 6/2/20.
//

#ifndef BOOP_ADSRPROCESSOR_H
#define BOOP_ADSRPROCESSOR_H

#include "ISignalProcessor.h"
#include "../waveform/ADSREnvelope.h"

class ADSRProcessor : public ISignalProcessor {
public:
    void renderSignal(float *audioData, int32_t numFrames, int burstNum, bool isReleasing) override {
        LOGI("Burst No: %i", burstNum);
        if (isReleasing) {
            for (int i = 0; i < numFrames; ++i) {
                float amplitude = adsrEnvelope->getOnReleaseAmplitude(i + numFrames * burstNum);
                if (amplitude == -1) {
                    audioData[i] = 0;
                } else {
                    audioData[i] = amplitude;
                }
            }
        } else {
            for (int i = 0; i < numFrames; ++i) {
                audioData[i] = adsrEnvelope->getOnPressedAmplitude(i + numFrames * burstNum);
            }
        }
    }

    /**
     * Sets ADSR envelope component on oscillator
     * @param generator
     */
    void setEnvelope(std::shared_ptr<ADSREnvelope> generator) {
        adsrEnvelope = generator;
    }

    /**
     * Sets attack length of ADSR
     * @param numMillis
     */
    void setAttackLength(int frames) {
        this->adsrEnvelope->setAttackLength(frames);
    }

    /**
     * Sets decay length of ADSR
     * @param numMillis
     */
    void setDecayLength(int frames) {
        this->adsrEnvelope->setDecayLength(frames);
    }

    /**
     * Sets sustain amplitude of ADSR
     * @param amplitude
     */
    void setSustainedLevel(float amplitude) {
        this->adsrEnvelope->setSustainedAmplitude(amplitude);
    }

    /**
     * Sets release length of ADSR
     * @param numMillis
     */
    void setReleaseLength(int frames) {
        this->adsrEnvelope->setReleaseLength(frames);
    }

    virtual ~ADSRProcessor() = default;
private:
    std::shared_ptr<ADSREnvelope> adsrEnvelope;
};
#endif //BOOP_ADSRPROCESSOR_H
