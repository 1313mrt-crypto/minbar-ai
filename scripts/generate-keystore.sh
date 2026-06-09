#!/bin/bash

# Minbar AI - Keystore Generation
# برای signing Release APK

set -e

echo "🔐 Keystore Generation"
echo "====================="
echo ""

read -p "Keystore filename (default: release.keystore): " KEYSTORE_FILE
KEYSTORE_FILE=${KEYSTORE_FILE:-release.keystore}

read -p "Key alias (default: sokhanara-key): " KEY_ALIAS
KEY_ALIAS=${KEY_ALIAS:-sokhanara-key}

echo ""
echo "⚠️  Keep this keystore file SAFE!"
echo ""

keytool -genkey -v -keystore "$KEYSTORE_FILE" \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000 \
    -alias "$KEY_ALIAS"

if [ -f "$KEYSTORE_FILE" ]; then
    echo ""
    echo "✅ Keystore created!"
    echo ""
    echo "🔒 Encode for GitHub Actions:"
    echo "   base64 -i $KEYSTORE_FILE"
else
    echo "❌ Failed"
    exit 1
fi
