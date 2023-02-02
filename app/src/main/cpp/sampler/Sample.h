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
        sampleEnd.store(data.size());
    }
    void renderSignal(
        float *audioData, int32_t numFrames, int burstNum, bool isReleasing
    ) override {
        int start = sampleStart.load();
        int end = sampleEnd.load();
        for (int i = 0; i < numFrames; i++) {
            int32_t frame = burstNum * numFrames + i + start; // Shift frame by sampleStart
            if (mIsLooping) {
                audioData[i] *= mData[frame % numFrames]; // TODO: This doesn't seem right. This needs to incorporate sampleEnd to properly
            } else if (frame < end) {
                audioData[i] *= mData[frame];
            } else {
                audioData[i] = 0;
            }
        }
    };

    bool requiresRelease() override { return false; }

    void setLooping(bool doLooping) {
        mIsLooping.store(doLooping);
    }

    /**
     * Sets start frame of sample.
     * @param start - frame for sample to start playing.
     */
    void setSampleStart(int start) {
        if (start >= 0 && start < sampleEnd.load()) {
            sampleStart.store(start);
        } else {
            LOGE("Sample start needs to be in bounds [0, %d], received (%d)", sampleEnd.load(), start);
        }
    }

    /**
     * Sets end frame of sample.
     * @param end - frame for sample to stop playing.
     */
    void setSampleEnd(int end) {
        if (end > sampleStart.load() && end <= dataSize.load()) {
            sampleEnd.store(end);
        } else {
            LOGE("Sample end needs to be in bounds [%d, %d], received (%d)", sampleStart.load(), dataSize.load(), end);
        }
    }

    void setData(std::vector<float> data) {
        for (int i = 0; i < mData.size(); i++) {
            if (i < data.size()) {
                this->mData[i] = data[i];
            } else {
                this->mData[i] = 0;
            }
        }
        dataSize.store(data.size());
        sampleStart.store(0);
        sampleEnd.store(data.size());
    }

    void setData(std::array<float, kMaxSamples> data) {
        for (int i = 0; i < mData.size(); i++) {
            if (i < data.size()) {
                this->mData[i] = data[i];
            } else {
                this->mData[i] = 0;
            }
        }
        dataSize.store(data.size());
        sampleStart.store(0);
        sampleEnd.store(data.size());
    }

    virtual ~Sample() = default;
private:
    std::atomic<bool> mIsLooping { false };
    std::array<float,kMaxSamples> mData { 0 };
    std::atomic<int> dataSize { 0 };
    std::atomic<int> sampleStart { 0 };
    std::atomic<int> sampleEnd { 0 };
};

#endif //BOOP_SAMPLE_H
