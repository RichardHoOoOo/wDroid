# ωDroid
ωDroid is a random testing tool to detect WebView-induced bugs (ωBugs for short) in Android applications. The current prototype of ωDroid only targets at the UI inconsistency and resource usage errors induced by misaligned WebView lifecycles. ωDroid is built on top of [Android Monkey](https://developer.android.com/studio/test/monkey). For more information about ωDroid project, please visit our [website](http://home.cse.ust.hk/~jhuao/wDroid.html).

# Setup
Since ωDroid is built on top of [Android Monkey](https://developer.android.com/studio/test/monkey), it only requires you to install Android SDK. Before you run ωDroid, make sure to launch an Android emulator (API level >= 21) or connect to a physical device (API level >= 21).

Please export ANDROID_HOME and configure PATH to let OS identify `adb` command.

Example:

`export ANDROID_HOME=your SDK path`  
`export PATH=$PATH:$ANDROID_HOME/platform-tools`


# Usage
If you want to use ωDroid in a simple way (i.e., use the built version we alrady made), please following the steps below:

+ Download the [latest version](https://github.com/RichardHoOoOo/wDroid/releases/latest) of ωDroid and decompress it.
+ Enter the folder you just downloaded from terminal.
+ Input command `sh run.sh device_id app_name package_name running_time apk_path output_path`
  - device_id: the ID of the device on which you plan to install the app under test (e.g., emulator-5554).
  - app_name: the name of the app under test (Please note for the app whose name include while space, replace all the white spaces with "_". For example, an app whose name is "My App" should be transformed to  "My_App").
  - package_name: the package name of the app under test.
  - running_time: the running time of ωDroid in seconds.
  - apk_path: the path of the input apk (e.g., /document/myApp.apk)
  - output_path: the output path of ωDroid.
  
 Example:
 
 `sh run.sh emulator-5554 WordPress org.wordpress.android 3600 /Users/abc/org.wordpress.android.apk /Users/abc/output
`

If you want to understand or modify the source code of ωDroid and build it by yourself, please following the steps below:

+ Firstly, you should download and build the Android source following the official documentation provided by Android team (This step may take hours):
  - [Requirements](https://source.android.com/setup/build/requirements)
  - [Establishing a Build Environment](https://source.android.com/setup/build/initializing)
  - [Downloading the Source](https://source.android.com/setup/build/downloading)
  - [Preparing to Build](https://source.android.com/setup/build/building)
+ Clone the ωDroid project `git clone https://github.com/RichardHoOoOo/wDroid.git`
+ Replace the content of AndroidSource/development/cmds/monkey/src/com/android/commands/monkey/ with the content of the ωDroid folder your cloned.
+ Build monkey from the root of the Android source (This step only takes 1-2 minutes):
  - `source build/envsetup.sh`
  - `lunch aosp_arm-eng`
  - `make monkey -j32`
+ Copy AndroidSource/out/target/common/obj/JAVA_LIBRARIES/monkey_intermediates/javalib.jar
+ Replace the monkey.jar in the [released version](https://github.com/RichardHoOoOo/wDroid/releases/latest) you downloaded with javalib.jar.
+ Rename javalib.jar to monkey.jar.
+ Execute run.sh following the steps we mentioned before.

# Result
The result of ωDroid is in the output path you specified. It contains a log file (event trace) and a folder with a bug report and screenshots.
