// Based on MonoToStereo from Oboe samples
// https://github.com/google/oboe/blob/master/samples/shared/MonoToStereo.h

#ifndef BOOP_CORE_MONO_TO_STEREO_H
#define BOOP_CORE_MONO_TO_STEREO_H

#include "IRenderableAudio.h"

/**
 * Renderable audio that translates mono input data to stereo
 */
class MonoToStereo : public IRenderableAudio {

public:

    /**
     * Constructor
     * @param i - input source
     */
    MonoToStereo(IRenderableAudio *i) : input(i){};

    // From IRenderableAudio
    void renderAudio(float *audioData, int32_t numFrames) override {

        constexpr int kChannelCountStereo = 2;

        input->renderAudio(audioData, numFrames);

        // We assume that audioData has sufficient frames to hold the stereo output, so copy each
        // frame in the input to the output twice, working our way backwards through the input array
        // e.g. 123 => 112233
        for (int i = numFrames - 1; i >= 0; --i) {

            audioData[i * kChannelCountStereo] = audioData[i];
            audioData[i * kChannelCountStereo + 1] = audioData[i];
        }
    }

    IRenderableAudio *input;
};


#endif //BOOP_CORE_MONO_TO_STEREO_H
