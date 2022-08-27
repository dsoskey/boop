//
// Created by Daniel Soskey on 6/18/20.
//

#ifndef BOOP_WAVESAMPLER_H
#define BOOP_WAVESAMPLER_H

#include <array>
#include <atomic>
#include <memory>

class WaveSampler {
public:
    void setRecording(bool isRecording) {
        this->isRecording.store(isRecording);
        if (isRecording) {
            recordingCursor = 0;
            memset(data.data(), 0, data.size()); // This sets values to \0, the null terminator
        }
    }

    std::array<float, kMaxSamples> getSample() {
        return data;
    }

    std::vector<float> asVector() {
        int firstSilenceIndex = data.size() - 1;
        // This might be stopping at the null terminator
        while (firstSilenceIndex >= 0 && data[firstSilenceIndex] == 0) firstSilenceIndex--;
        std::vector<float> choppedSample = std::vector<float>(firstSilenceIndex, 0);
        for(int i = 0; i < firstSilenceIndex; i++) {
            choppedSample[i] = data[i];
        }
        return choppedSample; // potential memory leak
    }

    int write(const float* sourceData, int32_t numFrames) {
        if (recordingCursor + numFrames > kMaxSamples) {
            numFrames = kMaxSamples - recordingCursor;
        }

        if (isRecording.load()) {
            for (int i = 0; i < numFrames; ++i) {
                data[recordingCursor++] = sourceData[i];
            }
        }
        return numFrames;

    }

private:
    std::atomic<bool> isRecording { false };
    int recordingCursor = 0;
    std::array<float, kMaxSamples> data { 0 };
};

#endif //BOOP_WAVESAMPLER_H
