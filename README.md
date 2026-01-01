# 🎤 سخنرانی هوشمند (Sokhanara)

## 📖 درباره پروژه

**سخنرانی هوشمند** یک اپلیکیشن Android پیشرفته است که با استفاده از هوش مصنوعی، سخنرانی‌های روشمند فارسی و عربی تولید می‌کند.

### ✨ ویژگی‌های کلیدی

#### 🔥 فاز ۱ (MVP)
- ✅ تولید سخنرانی ۵ مرحله‌ای (انگیزه‌سازی، اقناع، احساس، رفتار، روضه)
- ✅ پشتیبانی از منابع معتبر (PDF, DOCX, TXT, Web)
- ✅ **تحلیل عاطفی صدا** (منحصربفرد!)
- ✅ **بانک موضوعات مذهبی** با منابع معتبر
- ✅ **حالت آفلاین کامل**
- ✅ تم‌های بصری متنوع (محرم، رمضان، عید)
- ✅ خروجی PDF, PPTX, Infographic, MP3
- ✅ TTS فارسی با فراز و فرود (SSML)

#### 🚀 فاز ۲
- 🔜 حالت زنده (Live Speech Mode)
- 🔜 AI Coach (دستیار شخصی)
- 🔜 پروفایل سخنران
- 🔜 اشتراک‌گذاری هوشمند

#### 🌟 فاز ۳
- 🔜 بازار محتوا (Marketplace)
- 🔜 حالت همکاری (Collaboration)
- 🔜 پشتیبانی کامل از عربی
- 🔜 حالت مسابقه

---

## 🏗️ معماری

پروژه بر اساس **Clean Architecture** و **MVVM Pattern** ساخته شده:
├── ui/          → Jetpack Compose (UI Layer)
├── domain/      → Use Cases & Models (Business Logic)
├── data/        → Repository & Data Sources
├── ai/          → AI Engine (Online & Offline)
└── services/    → TTS, Export, Media
---

## 🛠️ تکنولوژی‌های استفاده‌شده

### Core
- **Kotlin** 1.9.20
- **Jetpack Compose** (Material 3)
- **Hilt** (Dependency Injection)
- **Coroutines & Flow**

### Data
- **Room** (Local Database)
- **DataStore** (Preferences)
- **Retrofit** (API Calls)

### AI & ML
- **ONNX Runtime** (Offline AI)
- **Google Cloud TTS** (Text-to-Speech)
- **Apache POI** (PowerPoint)
- **iText** (PDF Generation)

### Media
- **ExoPlayer** (Audio Playback)
- **Tarsos DSP** (Pitch Detection)

---

## 📦 نصب و راه‌اندازی

### پیش‌نیازها
- Android Studio Hedgehog | 2023.1.1+
- JDK 17
- Android SDK 34
- Gradle 8.2+

### مراحل نصب

1. Clone کردن پروژه:
```bash
git clone https://github.com/your-username/sokhanara-app.git
cd sokhanara-app
باز کردن در Android Studio
Sync Gradle:
./gradlew build
اجرا روی دستگاه/امولاتور:
./gradlew installDebug