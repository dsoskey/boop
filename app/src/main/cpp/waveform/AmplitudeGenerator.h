//
// Created by yello on 4/20/2020.
//

#ifndef BOOPK_AMPLITUDEGENERATOR_H
#define BOOPK_AMPLITUDEGENERATOR_H

class AmplitudeGenerator {
public:
    virtual float getOnPressedAmplitude(int frame) = 0;
    virtual float getOnReleaseAmplitude(int frame) = 0;
};

#endif //BOOPK_AMPLITUDEGENERATOR_H
