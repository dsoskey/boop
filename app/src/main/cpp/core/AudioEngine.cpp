#include "AudioEngine.h"
#include "Synth.h"
#include "../log.h"

AudioEngine::AudioEngine(std::vector<int> cpuIds) {
    createCallback(cpuIds);
    createRecordingCallback();
    start();
}

void AudioEngine::restart() {
    start();
}

std::shared_ptr<Synth> AudioEngine::getSynth() {
    return audioSource;
}

// TODO: look at builder documentation to understand each setting
oboe::Result AudioEngine::createPlaybackStream() {
    oboe::AudioStreamBuilder builder;
    return builder.setSharingMode(oboe::SharingMode::Exclusive)
            ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
            ->setFormat(oboe::AudioFormat::Float)
            ->setChannelCount(1)
            ->setCallback(callback.get())
            ->openManagedStream(stream);
}

oboe::Result AudioEngine::createRecordingStream(int32_t sampleRate) {
    oboe::AudioStreamBuilder builder;
    return builder.setDirection(oboe::Direction::Input)
            ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
            ->setFormat(oboe::AudioFormat::Float)
            ->setChannelCount(1)
            ->setSampleRate(sampleRate)
            ->setCallback(recordingCallback.get())
            ->openManagedStream(recordingStream);
}

void AudioEngine::createRecordingCallback() {
    recordingCallback = std::make_unique<DefaultRecordingStreamCallback>();
}

void AudioEngine::createCallback(std::vector<int> cpuIds) {
    callback = std::make_unique<DefaultAudioStreamCallback>(*this);
    callback->setCpuIds(cpuIds);
    callback->setThreadAffinityEnabled(true);
}

// TODO: Extract Synth creation from AudioEngine
void AudioEngine::start() {
    auto result = createPlaybackStream();
    if (result == oboe::Result::OK) {
        audioSource = std::make_shared<Synth>(stream->getSampleRate(), stream->getChannelCount());
        callback->setSource(std::dynamic_pointer_cast<IRenderableAudio>(audioSource));
        stream->start();

        result = createRecordingStream(stream->getSampleRate());
        if(result == oboe::Result::OK) {
            sampler = std::make_shared<WaveSampler>();
            recordingCallback->setTarget(sampler);
            recordingStream->start();
        } else {
            LOGE("Failed to create the recording stream. Error: %s", convertToText(result));
        }
    } else {
        LOGE("Failed to create the playback stream. Error: %s", convertToText(result));
    }
}

void AudioEngine::startRecordingSample(int oscIndex) {
    if (recordingIndex == -1) {
        recordingIndex = oscIndex;
        sampler->setRecording(true);
    }
}

std::vector<float> AudioEngine::stopRecordingSample() {
    std::vector<float> choppedSample = sampler->asVector();
    audioSource->setSample(recordingIndex, choppedSample);
    sampler->setRecording(false);
    recordingIndex = -1;
    return choppedSample;
}