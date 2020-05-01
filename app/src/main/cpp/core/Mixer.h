// Based on Mixer from Oboe samples
// https://github.com/google/oboe/blob/master/samples/shared/Mixer.h

#ifndef BOOP_CORE_MIXER_H
#define BOOP_CORE_MIXER_H

#include <array>
#include "IRenderableAudio.h"

constexpr int32_t kBufferSize = 192*10;  // Temporary buffer is used for mixing
constexpr uint8_t kMaxTracks = 128;

/**
 * A Mixer object which sums the output from multiple tracks into a single output. The number of
 * input channels on each track must match the number of output channels (default 1=mono). This can
 * be changed by calling `setChannelCount`.
 * The inputs to the mixer are not owned by the mixer, they should not be deleted while rendering.
 */
class Mixer : public IRenderableAudio {
public:
    // From IRenderableAudio
    void renderAudio(float *audioData, int32_t numFrames) {

        // Zero out the incoming container array
        memset(audioData, 0, sizeof(float) * numFrames * mChannelCount);

        for (int i = 0; i < mNextFreeTrackIndex; ++i) {
            mTracks[i]->renderAudio(mixingBuffer, numFrames);

            for (int j = 0; j < numFrames * mChannelCount; ++j) {
                audioData[j] += mixingBuffer[j];
            }
        }
    }

    /**
     * Adds a renderable track to the mixer
     * @param renderer
     */
    void addTrack(IRenderableAudio *renderer){
        mTracks[mNextFreeTrackIndex++] = renderer;
    }

    /**
     * Sets mono or stereo
     * @param channelCount
     */
    void setChannelCount(int32_t channelCount){ mChannelCount = channelCount; }

private:
    float mixingBuffer[kBufferSize];
    std::array<IRenderableAudio*, kMaxTracks> mTracks;
    uint8_t mNextFreeTrackIndex = 0;
    int32_t mChannelCount = 1; // Default to mono
};

#endif //BOOP_CORE_MIXER_H
