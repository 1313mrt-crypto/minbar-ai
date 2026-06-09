# GitHub Actions & APK Build Guide

## 📊 Project Status

✅ **Build Issues Fixed:**
- Apache POI compatibility (5.0.0)
- minSdk updated to 28
- ProGuard rules added
- Dependencies aligned

## 🚀 Quick Build

### Debug APK (Local)
```bash
chmod +x gradlew
./gradlew assembleDebug
```

**Output:** `app/build/outputs/apk/debug/app-debug.apk`

### Release APK (Local)
```bash
chmod +x scripts/build-release.sh
./scripts/build-release.sh
```

**Output:** `app/build/outputs/apk/release/`

## 🔐 Signing Setup

### 1. Generate Keystore
```bash
chmod +x scripts/generate-keystore.sh
./scripts/generate-keystore.sh
```

### 2. Encode for GitHub
```bash
base64 -i release.keystore
```

### 3. Add GitHub Secrets
Go to: `Settings → Secrets and variables → Actions`

Add these secrets:
- `KEYSTORE_BASE64`: Base64 encoded keystore
- `KEYSTORE_PASSWORD`: Your password
- `KEY_ALIAS`: sokhanara-key
- `KEY_PASSWORD`: Your key password

## 📂 Project Structure

```
minbar-ai/
├── app/
│   ├── src/main/java/com/sokhanara/app/
│   ├── build/outputs/apk/
│   └── build.gradle.kts
├── buildSrc/
│   └── src/main/kotlin/
│       ├── Versions.kt ✅ Fixed
│       └── Dependencies.kt
├── proguard-rules.pro ✅ Added
├── scripts/
│   ├── build-release.sh ✅ Added
│   └── generate-keystore.sh ✅ Added
└── docs/
    └── CICD_SETUP.md ✅ Added
```

## ✅ Changes Made

| File | Change | Status |
|------|--------|--------|
| Versions.kt | minSdk: 24→28, poi: 5.2.5→5.0.0 | ✅ |
| proguard-rules.pro | ProGuard config | ✅ Added |
| build-release.sh | Build script | ✅ Added |
| generate-keystore.sh | Keystore gen | ✅ Added |
| CICD_SETUP.md | Documentation | ✅ Added |

## 🧪 Troubleshooting

### Build fails with Gradle error
```bash
./gradlew clean
./gradlew --refresh-dependencies
./gradlew assembleDebug
```

### Cannot execute scripts
```bash
chmod +x scripts/*.sh
```

### Java version error
```bash
java -version  # Should be 17+
```

## 📱 Download APKs

**GitHub Actions:** https://github.com/1313mrt-crypto/minbar-ai/actions

**Artifacts** (after build completes):
- Debug APK (30 days retention)
- Test Results
- Lint Reports

---

**Last Updated:** June 9, 2026  
**Branch:** ci/github-actions → Ready to merge
