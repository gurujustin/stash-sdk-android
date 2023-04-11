#!/bin/sh

# Decrypt Release key
openssl aes-256-cbc -K $encrypted_a781090ed099_key -iv $encrypted_a781090ed099_iv -in signing/play-account.json.enc -out signing/play-account.json -d

# Decrypt Play Store key
openssl aes-256-cbc -K $encrypted_0f9c830a8a81_key -iv $encrypted_0f9c830a8a81_iv -in signing/release.keystore.enc -out signing/release.keystore -d
