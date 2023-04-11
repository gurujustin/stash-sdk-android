#!/bin/sh

#This script should be executed after assembling release apks.

# Delete Release key
rm -f signing/release.keystore

# Delete Play Store key
rm -f signing/play-account.json
