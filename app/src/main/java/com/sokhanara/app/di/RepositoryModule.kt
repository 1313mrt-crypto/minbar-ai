package com.sokhanara.app.di

import com.sokhanara.app.data.repository.*
import com.sokhanara.app.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Repository Module
 * تزریق وابستگی Repository‌ها
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindSpeechRepository(
        impl: SpeechRepositoryImpl
    ): SpeechRepository
    
    @Binds
    @Singleton
    abstract fun bindLibraryRepository(
        impl: LibraryRepositoryImpl
    ): LibraryRepository
    
    @Binds
    @Singleton
    abstract fun bindEmotionRepository(
        impl: EmotionRepositoryImpl
    ): EmotionRepository
    
    @Binds
    @Singleton
    abstract fun bindSourceRepository(
        impl: SourceRepositoryImpl
    ): SourceRepository
    
    @Binds
    @Singleton
    abstract fun bindExportRepository(
        impl: ExportRepositoryImpl
    ): ExportRepository
}
```

---

## ۲. ساخت پوشه `assets/fonts/` و کپی فونت

### مرحله ۱: ساخت پوشه در GitHub

در GitHub، این مسیر رو بساز:
```
app/src/main/assets/fonts/
```

### مرحله ۲: کپی فایل فونت

فایل `vazir_regular.ttf` که قبلاً در اینجا گذاشتی:
```
app/src/main/res/font/vazir_regular.ttf
```

یک کپی‌ش رو اینجا هم بذار:
```
app/src/main/assets/fonts/vazir_regular.ttf
