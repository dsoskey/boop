# boop

boop is a software synthesizer.

## Development

### Setting up your workspace

1. Clone the repository. Run `git clone git@github.com:dsoskey/boop.git`
1. Add in oboe as a submodule.
   1. Run `git submodule init`
   1. Run `git submodule update`
1. Download Android Studio and open it up
1. Import boop into Android Studio
1. Click `Tools > SDK Manager` and install the following SDK tools:
    - NDK (Side by side): version 21.0.6113669
    - CMake: minimum version 3.4.1
    - [Google USB Driver](https://developer.android.com/studio/run/win-usb). Do this one only if you are on Windows.
1. [Create a signing key](https://developer.android.com/studio/publish/app-signing#generate-key)
1. Create a file named `keystore.properties` at the root of boop with the following data:
```
storePassword=<password used in keystore>
keyPassword=<password used for signing key>
keyAlias=boop
storeFile=<Path to your keystore>
```
