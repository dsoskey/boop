// Based on IRenderableAudio from Oboe samples
// https://github.com/google/oboe/blob/master/samples/shared/IRenderableAudio.h

#ifndef BOOP_CORE_IRENDERABLEAUDIO_H
#define BOOP_CORE_IRENDERABLEAUDIO_H

/**
 * Represents an object that can render floating-point audio signal data.
 * Consumers call renderAudio directly or pass it into an oboe callback to be called when an
 * audio stream needs more data.
 */
class IRenderableAudio {

public:
    virtual ~IRenderableAudio() = default;
    virtual void renderAudio(float *audioData, int32_t numFrames) = 0;
};

#endif //BOOP_CORE_IRENDERABLEAUDIO_H
