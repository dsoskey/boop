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
    /**
     * Method called by signal chain for transforming the signal
     * @param audioData - array containing the data to transform.
     * @param numFrames - number of frames in the audio data.
     * @param burstNum - represents how many times renderSignal has been called during this triggering of the signal chain.
     * @param isReleasing - whether or not chain is in release state.
     */
    virtual void renderSignal(float *audioData, int32_t numFrames, int burstNum, bool isReleasing) = 0;

    /**
     * Called by signal chain to determine its on/off strategy.
     * @return true if this processor uses release, false if processor uses on/off
     */
    bool usesRelease() {
        return this->currentlyUsesRelease;
    };

    /**
     * Sets signal on/off and release strategy
     * @param isOn - whether or not signal processor is on.
     */
    void setSignalOn(bool isOn) {
        this->isOn.store(isOn);
        if (isOn) {
            this->currentlyUsesRelease = this->requiresRelease();
        } else {
            this->currentlyUsesRelease = false;
        }
    }

    /**
     * Indicates to signal chain if it should be rendered or not.
     * @return - whether or not signal processor is on.
     */
    bool isSignalOn() {
        return isOn.load();
    }

protected:
    /**
     * Called by signal chain to determine its on/off strategy.
     * @return true if this processor uses release, false if processor uses on/off
     */
    virtual bool requiresRelease() = 0;
    /**
     * Stores state of whether or not to use release strategy at the moment.
     */
    bool currentlyUsesRelease{ false };
private:
    std::atomic<bool> isOn { true };
};
#endif //BOOP_ISIGNALPROCESSOR_H
