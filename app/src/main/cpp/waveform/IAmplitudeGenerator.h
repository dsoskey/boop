#ifndef BOOPK_IAMPLITUDEGENERATOR_H
#define BOOPK_IAMPLITUDEGENERATOR_H

/**
 * Represents an envelope that provides an amplitude as a function of time.
 * Split into two parts of a button interaction lifecycle, press and release.
 */
class IAmplitudeGenerator {
public:
    /**
     * Returns the amplitude for a given frame when synth is turned on
     * @param frame number
     * @return amplitude at frame
     */
    virtual float getOnPressedAmplitude(int frame) = 0;

    /**
     * Returns the amplitude for a given frame when synth is turned off
     * @param frame number
     * @return amplitude at frame
     */
    virtual float getOnReleaseAmplitude(int frame) = 0;
};

#endif //BOOPK_IAMPLITUDEGENERATOR_H
