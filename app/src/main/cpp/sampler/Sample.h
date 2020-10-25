//
// Created by Daniel Soskey on 6/18/20.
//

#ifndef BOOP_SAMPLE_H
#define BOOP_SAMPLE_H

#include "../core/ISignalProcessor.h"
#include <cstdint>
#include <array>
#include <atomic>
#include "../log.h"

constexpr int kMaxSamples = 480000; // 10s of audio data @ 48kHz

// TODO: dataSize needs to be properly used
class Sample : public ISignalProcessor {
public:
    Sample(std::array<float, kMaxSamples> data) {
        for (int i = 0; i < mData.size(); i++) {
            if (i < data.size()) {
                this->mData[i] = data[i];
            } else {
                this->mData[i] = 0;
            }
        }
        dataSize.store(data.size());
    }
    void renderSignal(
        float *audioData, int32_t numFrames, int burstNum, bool isReleasing
    ) override {
        for (int i = 0; i < numFrames; i++) {
            int32_t frame = burstNum * numFrames + i;
            if (mIsLooping) {
                audioData[i] *= mData[frame % numFrames]; // TODO: This doesn't seem right. This needs to incorporate dataSize to properly
            } else if (frame < mData.size()) {
                audioData[i] *= mData[frame];
            } else {
                audioData[i] = 0;
            }
        }
    };

    void setLooping(bool doLooping) {
        mIsLooping.store(doLooping);
    }

    void setData(std::vector<float> data) {
        for (int i = 0; i < mData.size(); i++) {
            if (i < data.size()) {
                this->mData[i] = data[i];
            } else {
                this->mData[i] = 0;
            }
        }
    }

    void setData(std::array<float, kMaxSamples> data) {
        for (int i = 0; i < mData.size(); i++) {
            if (i < data.size()) {
                this->mData[i] = data[i];
            } else {
                this->mData[i] = 0;
            }
        }
    }

    virtual ~Sample() = default;
private:
    std::atomic<bool> mIsLooping { false };
    std::array<float,kMaxSamples> mData { 0 };
    std::atomic<int> dataSize { 0 };
};

#endif //BOOP_SAMPLE_H
