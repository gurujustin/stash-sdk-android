#!/bin/bash
echo no | android create avd --force -n test -t android-21 --abi armeabi-v7a
    emulator -avd test -no-audio -no-window &
    android-wait-for-emulator
    adb shell input keyevent 82 &
    adb shell settings put global stay_on_while_plugged_in 3