# CI/CD Setup Guide - سخنرانی هوشمند

## 📋 فهرست

1. [GitHub Actions Setup](#github-actions-setup)
2. [Keystore Generation](#keystore-generation)
3. [GitHub Secrets](#github-secrets)
4. [Build APK Locally](#build-apk-locally)

---

## 🚀 GitHub Actions Setup

Workflow فعلی به صورت خودکار بر روی هر push/PR اجرا می‌شود:

- ✅ Gradle Sync
- ✅ Debug APK Build
- ✅ Unit Tests
- ✅ Lint Analysis
- ✅ Artifact Upload

### View Workflows

```bash
# مشاهده تمام workflows
https://github.com/1313mrt-crypto/minbar-ai/actions

# مشاهده آخرین build
https://github.com/1313mrt-crypto/minbar-ai/actions/workflows/build-apk.yml
```

---

## 🔐 Keystore Generation

برای ساخت Release APK امضا‌شده:

### Step 1: Generate Keystore

```bash
chmod +x scripts/generate-keystore.sh
./scripts/generate-keystore.sh
```

### Step 2: Encode Keystore

```bash
base64 -i release.keystore
```

### Step 3: Add GitHub Secrets

1. به repository settings بروید
2. Secrets → Actions
3. اضافه کنید:

| Secret Name | Value |
|---|---|
| `KEYSTORE_BASE64` | Base64 encoded keystore |
| `KEYSTORE_PASSWORD` | Your keystore password |
| `KEY_ALIAS` | sokhanara-key |
| `KEY_PASSWORD` | Your key password |

---

## 📦 Build APK Locally

### Debug APK

```bash
./gradlew assembleDebug
```

خروجی: `app/build/outputs/apk/debug/app-debug.apk`

### Release APK (Unsigned)

```bash
./gradlew assembleRelease
```

خروجی: `app/build/outputs/apk/release/app-release-unsigned.apk`

### Release APK (Signed)

```bash
chmod +x scripts/build-release.sh
./scripts/build-release.sh
```

---

## 📊 Troubleshooting

### Build Fails with Gradle Error

```bash
./gradlew clean
./gradlew --refresh-dependencies
./gradlew assembleDebug
```

### Java Version Issues

```bash
java -version
# Should be 17+
```

### Gradle Wrapper Issues

```bash
chmod +x gradlew
./gradlew --version
```

---

## 📝 Notes

- توجه: keystore فایل را commit نکنید
- secrets برای CI/CD استفاده می‌شود
- APK artifacts 30 روز نگهداری می‌شوند

---

**Last Updated:** June 9, 2026
**Branch:** ci/github-actions
