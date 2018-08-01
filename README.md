# ωDroid
ωDroid is a random testing tool to detect WebView-induced bugs (ωBugs for short) in Android applications. The current prototype of ωDroid only targets at the UI inconsistency and resource usage errors induced by misaligned WebView lifecycles. ωDroid is built on top of [Android Monkey](https://developer.android.com/studio/test/monkey). For more information about ωDroid project, please visit our [website](http://home.cse.ust.hk/~jhuao/wDroid.html).

# Setup
Since ωDroid is built on top of [Android Monkey](https://developer.android.com/studio/test/monkey), it only requires you to install Android SDK. Before you run ωDroid, make sure to launch an Android emulator (API level >= 21) or connect to a physical device (API level >= 21).

Please export ANDROID_HOME and configure PATH to let the OS identify `adb` command.

Example:

`export ANDROID_HOME=your SDK path`  
`export PATH=$PATH:$ANDROID_HOME/platform-tools`


## Usage
1. For the app whose name include while space, replace all the white spaces with "_". For example, an app whose name is "My App" should be transformed to  "My_App".
