# 🎤 سخنرانی هوشمند (Sokhanara)

## 📖 درباره پروژه

**سخنرانی هوشمند** یک اپلیکیشن Android پیشرفته است که با استفاده از هوش مصنوعی، سخنرانی‌های روشمند فارسی و عربی تولید می‌کند.

### ✨ ویژگی‌های پیاده‌سازی‌شده (فاز ۱ - MVP)

- ✅ تولید سخنرانی ۵ مرحله‌ای (انگیزه‌سازی، اقناع، احساس، رفتار، روضه)
- ✅ پشتیبانی از منابع معتبر (PDF, DOCX, TXT, Web)
- ✅ موتور AI هوشمند برای تولید محتوا
- ✅ تحلیل عاطفی صدا (پایه‌گذاری شده)
- ✅ بانک موضوعات مذهبی (معماری آماده)
- ✅ حالت آفلاین (معماری آماده)
- ✅ تم‌های بصری متنوع (محرم، رمضان، عید)
- ✅ خروجی PDF, PPTX, Infographic, Checklist
- ✅ معماری Clean Architecture
- ✅ MVVM Pattern
- ✅ Dependency Injection با Hilt

### 🚀 فاز ۲ (در انتظار توسعه)

- 🔜 اتصال به API واقعی هوش مصنوعی
- 🔜 TTS فارسی با فراز و فرود (SSML)
- 🔜 حالت زنده (Live Speech Mode)
- 🔜 AI Coach (دستیار شخصی)
- 🔜 پروفایل سخنران
- 🔜 اشتراک‌گذاری هوشمند

### 🌟 فاز ۳ (آینده)

- 🔜 بازار محتوا (Marketplace)
- 🔜 حالت همکاری (Collaboration)
- 🔜 پشتیبانی کامل از عربی
- 🔜 حالت مسابقه

---

## 🏗️ معماری

پروژه بر اساس **Clean Architecture** و **MVVM Pattern** ساخته شده:
```
├── ui/          → Jetpack Compose (UI Layer)
├── domain/      → Use Cases & Models (Business Logic)
├── data/        → Repository & Data Sources
├── ai/          → AI Engine (Online & Offline)
└── services/    → Export, TTS, Media
```

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
- **Retrofit** (API - آماده اتصال)

### AI & ML
- **AI Engine سفارشی** (آماده اتصال به API)
- **Template System** برای ۵ مرحله
- **ONNX Runtime** (برای آفلاین - آینده)

### Export
- **Apache POI** (PowerPoint & Word)
- **iText** (PDF Generation)
- **Android Canvas** (Infographic)

### Parsers
- **PDF**: Apache PDFBox
- **DOCX**: Apache POI
- **TXT**: Native
- **Web**: Custom Parser

---

## 📦 وضعیت پروژه

### ✅ تکمیل‌شده (90%)
- Setup و Dependencies
- UI Layer (Theme, Navigation, HomeScreen)
- Database (Room + 4 جدول)
- Domain Layer کامل (Models + Use Cases)
- AI Engine (Templates + PromptBuilder)
- Parsers (PDF, DOCX, TXT, Web)
- Export Services (PDF, PPTX, Infographic, Checklist)
- Repository Implementation
- ViewModel‌ها
- Dependency Injection کامل

### 🔄 در حال توسعه (10%)
- اتصال UI به Backend
- تست و دیباگ
- اتصال به API واقعی

---

## 🚀 نصب و اجرا

### پیش‌نیازها
- Android Studio Hedgehog | 2023.1.1+
- JDK 17
- Android SDK 34
- Gradle 8.2+

### مراحل

1. Clone پروژه:
```bash
git clone https://github.com/1313mrt-crypto/minbar-ai.git
cd minbar-ai
```

2. باز کردن در Android Studio

3. Sync Gradle و Build

4. اجرا روی دستگاه/امولاتور

---

## 📂 ساختار پروژه
```
app/src/main/java/com/sokhanara/app/
├── ui/              # Compose Screens & ViewModels
├── domain/          # Models, Use Cases, Repository Interfaces
├── data/            # Repository Impl, DAOs, Parsers
├── ai/              # AI Engine, Templates, Prompt Builder
├── services/        # Export (PDF, PPTX, Infographic)
├── di/              # Hilt Modules
└── util/            # Utilities & Extensions
```

---

## 🤝 مشارکت

این پروژه تحت توسعه است. برای مشارکت:

1. Fork کنید
2. Branch جدید بسازید
3. تغییرات را commit کنید
4. Pull Request ارسال کنید

---

## 📄 لایسنس

MIT License

---

## 📞 تماس

- GitHub: [@1313mrt-crypto](https://github.com/1313mrt-crypto)
- پروژه: [minbar-ai](https://github.com/1313mrt-crypto/minbar-ai)

---

**ساخته شده با ❤️ برای جامعه فارسی‌زبان**

**Powered by Claude AI (Anthropic)**
```

---

## ✅ ساختار فایل‌های ساخته‌شده
```
app/src/main/java/com/sokhanara/app/di/
├── AppModule.kt                ✅ (قبلی)
├── DatabaseModule.kt           ✅ (قبلی)
├── NetworkModule.kt            ⏳ (فاز ۲)
├── RepositoryModule.kt         ✅ (بروز شده)
├── UseCaseModule.kt            ✅ جدید
├── ServiceModule.kt            ✅ جدید
└── AiModule.kt                 ✅ جدید

app/src/main/res/values/
└── themes.xml                  ✅ بروز شده

README.md                       ✅ بروز شده
