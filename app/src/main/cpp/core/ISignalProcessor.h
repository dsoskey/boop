//
// Created by Daniel Soskey on 6/2/20.
//

#ifndef BOOP_ISIGNALPROCESSOR_H
#define BOOP_ISIGNALPROCESSOR_H

/**
 * Represents an object that can render floating-point audio as part of a signal chain.
 * Consumers call render* directly and the object writes to audioData.
 */
class ISignalProcessor {
public:
    virtual void renderSignal(float *audioData, int32_t numFrames, int burstNum, bool isReleasing) = 0;
    void setSignalOn(bool isOn) {
        this->isOn.store(isOn);
    }
    bool isSignalOn() {
        return isOn.load();
    }
private:
    std::atomic<bool> isOn { true };
};
#endif //BOOP_ISIGNALPROCESSOR_H
