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
🔑 تنظیمات API
Google Cloud TTS (اختیاری - برای صدای باکیفیت)
فایل local.properties بسازید:
GOOGLE_CLOUD_API_KEY=your_api_key_here
یا از حالت آفلاین استفاده کنید (بدون نیاز به API)
📂 ساختار پروژه
app/src/main/java/com/sokhanara/app/
├── ui/              # Screens & Components
├── domain/          # Use Cases & Models  
├── data/            # Repositories & DAOs
├── ai/              # AI Engine
├── services/        # Export, TTS, Media
├── di/              # Hilt Modules
└── util/            # Utilities
🎯 نقشه راه
[x] v1.0 - MVP با تولید سخنرانی ۵ مرحله‌ای
[x] v1.0 - تحلیل عاطفی صدا
[x] v1.0 - کتابخانه موضوعات
[x] v1.0 - حالت آفلاین
[ ] v1.5 - حالت زنده
[ ] v2.0 - AI Coach
[ ] v2.5 - بازار محتوا
[ ] v3.0 - همکاری تیمی
🤝 مشارکت
این پروژه Open Source است! خوشحال می‌شیم مشارکت کنید:
Fork کنید
Branch جدید بسازید (git checkout -b feature/amazing-feature)
Commit کنید (git commit -m 'Add amazing feature')
Push کنید (git push origin feature/amazing-feature)
Pull Request باز کنید
📄 لایسنس
این پروژه تحت لایسنس MIT منتشر شده. برای جزئیات بیشتر LICENSE را ببینید.
📞 تماس
وب‌سایت: sokhanara.ir
ایمیل: support@sokhanara.ir
تلگرام: @sokhanara_app
🙏 تشکر ویژه
Anthropic (Claude AI)
Google Cloud
کتابخانه‌های Open Source
ساخته شده با ❤️ برای جامعه فارسی‌زبان