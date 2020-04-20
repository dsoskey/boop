#include "AudioEngine.h"
#include "Synth.h"
#include "../log.h"

AudioEngine::AudioEngine(std::vector<int> cpuIds) {
    createCallback(cpuIds);
    start();
}

void AudioEngine::setSourceOn(int oscIndex, bool isOn) {
    audioSource->setWaveOn(oscIndex, isOn);
}

void AudioEngine::setFrequency(int oscIndex, double frequency) {
    audioSource->setFrequency(oscIndex, frequency);
}

void AudioEngine::setWaveform(int oscIndex, WaveGenerator *waveGenerator) {
    audioSource->setWave(oscIndex, waveGenerator);
}

void AudioEngine::restart() {
    start();
}

// TODO: look at builder documentation to understand each setting
oboe::Result AudioEngine::createPlaybackStream() {
    oboe::AudioStreamBuilder builder;
    return builder.setSharingMode(oboe::SharingMode::Exclusive)
            ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
            ->setFormat(oboe::AudioFormat::Float)
            ->setCallback(callback.get())
            ->openManagedStream(stream);
}

void AudioEngine::createCallback(std::vector<int> cpuIds) {
    // TODO: What is make_unique
    callback = std::make_unique<DefaultAudioStreamCallback>(*this);

    callback->setCpuIds(cpuIds);
    callback->setThreadAffinityEnabled(true);
}

void AudioEngine::start() {
    auto result = createPlaybackStream();
    if (result == oboe::Result::OK) {
        // TODO: What is make_shared
        audioSource = std::make_shared<Synth>(stream->getSampleRate(), stream->getChannelCount());

        callback->setSource(std::dynamic_pointer_cast<IRenderableAudio>(audioSource));
        stream->start();
    } else {
        LOGE("Failed to create the playback stream. Error: %s", convertToText(result));
    }
}

void AudioEngine::setAmplitude(int oscIndex, float amplitude) {
    audioSource->setAmplitude(oscIndex, amplitude);
}
