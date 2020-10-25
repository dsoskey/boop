#include "AudioEngine.h"
#include "Synth.h"
#include "../log.h"

AudioEngine::AudioEngine(std::vector<int> cpuIds) {
    createCallback(cpuIds);
    createRecordingCallback();
    start();
}

void AudioEngine::setSourceOn(int oscIndex, bool isOn) {
    audioSource->setWaveOn(oscIndex, isOn);
}

void AudioEngine::setFrequency(int oscIndex, double frequency) {
    audioSource->setFrequency(oscIndex, frequency);
}

void AudioEngine::setWaveform(int oscIndex, std::shared_ptr<WaveGenerator> waveGenerator) {
    audioSource->setWave(oscIndex, waveGenerator);
}

void AudioEngine::setSample(int oscIndex, std::vector<float> data) {
    audioSource->setSample(oscIndex, data);
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
            ->setCallback(recordingCallback.get()) // TODO: create recording callback. maybe not tho
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

void AudioEngine::setAmplitude(int oscIndex, float amplitude) {
    audioSource->setAmplitude(oscIndex, amplitude);
}

void AudioEngine::setAttackLength(int millis) {
    audioSource->setAttackLength(millis);
}

void AudioEngine::setDecayLength(int millis) {
    audioSource->setDecayLength(millis);
}

void AudioEngine::setSustainedLevel(float amplitude){
    audioSource->setSustainedLevel(amplitude);
}

void AudioEngine::setReleaseLength(int millis) {
    audioSource->setReleaseLength(millis);
}

void AudioEngine::startRecordingSample(int oscIndex) {
    if (recordingIndex == -1) {
        LOGE("LETS START");
        recordingIndex = oscIndex;
        sampler->setRecording(true);
    }
}

std::array<float, kMaxSamples> AudioEngine::stopRecordingSample() {
//    LOGE("STOP RECORDING: %i", recordingIndex);
    std::array<float, kMaxSamples> sample = sampler->getSample();
    audioSource->setSample(recordingIndex, sample);
    sampler->setRecording(false);
    recordingIndex = -1;
    return sample;
}