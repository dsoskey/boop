//
// Created by Daniel Soskey on 6/22/20.
//

#ifndef BOOP_DEFAULTRECORDINGSTREAMCALLBACK_H
#define BOOP_DEFAULTRECORDINGSTREAMCALLBACK_H

#include <oboe/AudioStreamCallback.h>
#include "WaveSampler.h"
#include <cstdint>

class DefaultRecordingStreamCallback : public oboe::AudioStreamCallback {
public:
    virtual oboe::DataCallbackResult onAudioReady(oboe::AudioStream *oboeStream, void *audioData, int32_t numFrames) override {
        float *inputBuffer = static_cast<float *>(audioData);
        if (!target) {
            LOGE("WaveSampler target not set!");
            return oboe::DataCallbackResult::Stop;
        }
        int numFramesWritten = target->write(inputBuffer, numFrames);
        if (numFramesWritten == 0) {
            target->setRecording(false);
        }
        return oboe::DataCallbackResult::Continue;
    }

    void setTarget(std::shared_ptr<WaveSampler> target) {
        LOGE("TARGET SET");
        this->target = target;
    }

    virtual ~DefaultRecordingStreamCallback() = default;
private:
    std::shared_ptr<WaveSampler> target;
};
#endif //BOOP_DEFAULTRECORDINGSTREAMCALLBACK_H
