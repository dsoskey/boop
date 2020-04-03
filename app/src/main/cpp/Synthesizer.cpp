//
// Created by yello on 3/28/2020.
//

#include "Synthesizer.h"

Synthesizer::Synthesizer(int numTracks) {
    tracks = *new std::vector<AudioEngine *>();
    for (int i = 0; i < numTracks; i++) {
        AudioEngine *audioEngine = new AudioEngine();
        audioEngine->setWave(new SinWaveformGenerator());
        tracks.push_back(audioEngine);
    }
    nextOpenTrack.store(0);
}


int Synthesizer::setToneOn(double frequency) {
    int currentTrack = nextOpenTrack.load();
    tracks[currentTrack]->setToneOn(frequency);
    freqToTrack[frequency] = currentTrack;
    int nextTrack = (currentTrack + 1) % tracks.size();
    nextOpenTrack.store(nextTrack);
    return nextTrack;
}

void Synthesizer::setToneOff(double frequency) {
    int track = freqToTrack.at(frequency);
    tracks[track]->setToneOff();
    nextOpenTrack.store(track);
}

void Synthesizer::setWave(WaveGenerator *waveGenerator) {
    for (int i = 0; i < tracks.size(); i++) {
        tracks[i]->setWave(waveGenerator);
    }
}

bool Synthesizer::startEngines() {
    bool allSucc = true;

    for (int i = 0; i < tracks.size(); i++) {
        bool result = tracks[i]->start();
        if (!result) {
            allSucc = false;
        }
    }
    return allSucc;
}

void Synthesizer::stopEngines() {
    for (int i = 0; i < tracks.size(); i++) {
        tracks[i]->stop();
    }
}