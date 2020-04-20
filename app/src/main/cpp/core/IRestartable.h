// Based on IRestartable from Oboe samples
// https://github.com/google/oboe/blob/master/samples/shared/IRestartable.h

#ifndef BOOP_CORE_IRESTARTABLE_H
#define BOOP_CORE_IRESTARTABLE_H

/**
 * Represents an object which can be restarted. For example an audio engine which has one or more
 * streams which can be restarted following a change in audio device configuration. For example,
 * headphones being connected.
 */
class IRestartable {
public:
    virtual void restart() = 0;
};

#endif //BOOP_CORE_IRESTARTABLE_H
