//
// Created by yello on 3/28/2020.
//

#ifndef BOOPK_SYNTHESIZER_H
#define BOOPK_SYNTHESIZER_H

#include <vector>
#include <map>
#include "AudioEngine.h"

class Synthesizer {
public:
    Synthesizer(int numTracks);
    int setToneOn(double frequency);
    void setToneOff(double frequency);
    void setWave(WaveGenerator* waveGenerator);
    bool startEngines();
    void stopEngines();
private:
    std::vector<AudioEngine *> tracks;
    std::atomic<int> nextOpenTrack;
    std::map<double, int> freqToTrack;
};


#endif //BOOPK_SYNTHESIZER_H
