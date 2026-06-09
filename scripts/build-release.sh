#!/bin/bash

# Minbar AI - Release Build Script
# این اسکریپت برای ساخت Release APK استفاده می‌شود

set -e

echo "🚀 Minbar AI - Release Build"
echo "================================"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Check prerequisites
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Java is not installed${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Java version:${NC}"
java -version

# Clean previous builds
echo -e "${YELLOW}🧹 Cleaning previous builds...${NC}"
./gradlew clean

# Build Debug APK
echo -e "${YELLOW}🏗️ Building Debug APK...${NC}"
./gradlew assembleDebug --stacktrace

if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    echo -e "${GREEN}✅ Debug APK built successfully${NC}"
    echo -e "   Path: app/build/outputs/apk/debug/app-debug.apk"
    du -h app/build/outputs/apk/debug/app-debug.apk
else
    echo -e "${RED}❌ Debug APK build failed${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}✅ Build completed successfully!${NC}"
echo ""
echo "📱 APKs location:"
echo "   Debug:  app/build/outputs/apk/debug/"
echo ""
