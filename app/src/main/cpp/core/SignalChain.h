//
// Created by Daniel Soskey on 6/2/20.
//

#ifndef BOOP_SIGNALCHAIN_H
#define BOOP_SIGNALCHAIN_H

#include <array>
#include <atomic>
#include <cstdint>
#include <memory>
#include "IRenderableAudio.h"
#include "Mixer.h"
#include "ISignalProcessor.h"

class SignalChain : public IRenderableAudio {
public:
    // From IRenderableAudio
    void renderAudio(float *audioData, int32_t numFrames) override {
        if (this->isOn.load()) {
            bool isSilent = true;
            float currentAmplitude = amplitude.load();
            for (int j = 0; j < numFrames; ++j) {
                audioData[j] = currentAmplitude;
            }
            for (int i = 0; i < mNextFreeTrackIndex; i++) {
                if (mChainArray[i]->isSignalOn()) {
                    mChainArray[i]->renderSignal(audioData, numFrames, currentBurst.load(), isReleasing.load());
                }
            }
            for (int j = 0; j < numFrames; ++j) {
                if(audioData[j] != 0) {
                    isSilent = false;
                }
            }
            if (isSilent) {
                isOn.store(false);
                currentBurst.store(0);
            } else {
                currentBurst.store(currentBurst.load() + 1);
            }
        } else {
            memset(audioData, 0, sizeof(float) * numFrames);
        }
    }

    /**
     * Sets signal chain on or off and whether chain is being pressed or released
     * @param isWaveOn
     */
    void setOn(bool isOn) {
        currentBurst.store(0);
        if (isOn) {
            this->isOn.store(true);
            isReleasing.store(false);
        } else if (this->shouldUseRelease()) {
            LOGE("RELEASING");
            isReleasing.store(true);
        } else {
            LOGE("OFFING");
            this->isOn.store(false);
        }
    };

    /**
     * Sets base amplitude of wave being generated
     * @param amplitude
     */
    void setAmplitude(float amplitude) {
        this->amplitude = amplitude;
    };

    /**
     * Adds a renderable track to the end of the signal chain.
     * @param renderer
     */
    uint8_t addRenderable(std::shared_ptr<ISignalProcessor> renderer){
        uint8_t index = mNextFreeTrackIndex;
        mChainArray[mNextFreeTrackIndex++] = renderer;
        return index;
    }

    /**
     * Gets a reference to a Signal Processor at the index position in the chain.
     * It is the caller's responsibility to know what type of Signal Processor is at each
     * IDEA: Type inflection
     * @param index
     * @return reference to signal processor at index.
     */
    std::shared_ptr<ISignalProcessor> getRenderable(unsigned int index) {
        return mChainArray[index];
    }

private:
    bool shouldUseRelease() {
        bool shouldRelease = false;
        for (int i = 0; i < mNextFreeTrackIndex; i++) {
            LOGE("Index: %d, usesRelease %d", i, mChainArray[i]->usesRelease() );
            if (mChainArray[i]->usesRelease()) {
                shouldRelease = true;
            }
        }
        return shouldRelease;
    }
    std::atomic<bool> isOn { false };
    std::atomic<int> currentBurst { 0 };
    std::atomic<bool> isReleasing { false };
    std::atomic<float> amplitude { 0 };

    std::array<std::shared_ptr<ISignalProcessor>, kMaxTracks> mChainArray;
    uint8_t mNextFreeTrackIndex = 0;
};

#endif //BOOP_SIGNALCHAIN_H
