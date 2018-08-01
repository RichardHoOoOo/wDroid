# ωDroid
ωDroid is a random testing tool to detect WebView-induced bugs (ωBugs for short) in Android applications. The current prototype of ωDroid only targets at the UI inconsistency and resource usage errors induced by misaligned WebView lifecycles. ωDroid is built on top of [Android Monkey](https://developer.android.com/studio/test/monkey). For more information about ωDroid project, please visit our [website](http://home.cse.ust.hk/~jhuao/wDroid.html).

# Setup
Since ωDroid is built on top of [Android Monkey](https://developer.android.com/studio/test/monkey), it only requires you to install Android SDK. Before you run ωDroid, make sure to launch an Android emulator (API level >= 21) or connect to a physical device (API level >= 21).

Please export ANDROID_HOME and configure PATH to let OS identify `adb` command.

Example:

`export ANDROID_HOME=your SDK path`  
`export PATH=$PATH:$ANDROID_HOME/platform-tools`


# Usage
+ Download the [latest version](https://github.com/RichardHoOoOo/wDroid/releases/latest) of ωDroid and decompress it.
+ Enter the folder you just downloaded from terminal.
+ Input command `sh run.sh device_id app_name package_name running_time apk_path output_path`
  - device_id: the ID of the device on which you plan to install app (e.g., emulator-5554).
  - app_name: the name of the app under test (Please note for the app whose name include while space, replace all the white spaces with "_". For example, an app whose name is "My App" should be transformed to  "My_App").
  - package_name: the package name of the app under test.
  - running_time: the running time of ωDroid in seconds.
  - apk_path: the path of the input apk (e.g., /document/myApp.apk)
  - output_path: the output path of ωDroid.
  
 Example:
 
 `sh run.sh emulator-5554 WordPress org.wordpress.android 3600 /Users/abc/org.wordpress.android.apk /Users/abc/output
`

