#!/usr/bin/env bash
# Simple script to clean the android platform, clean the plugin, and reinstall both fresh

cd harness
cordova platform remove android
cordova plugin remove cordova-plugin-xapp
cordova platform add android
cordova plugin add --link ../
