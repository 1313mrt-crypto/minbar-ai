package com.sokhanara.app.util

/**
 * Constants
 * ثابت‌های برنامه
 */
object Constants {
    
    // App Info
    const val APP_NAME = "سخنرانی هوشمند"
    const val APP_VERSION = "1.0.0"
    const val APP_PACKAGE = "com.sokhanara.app"
    
    // Database
    const val DATABASE_NAME = "sokhara_database"
    const val DATABASE_VERSION = 1
    
    // API
    const val API_BASE_URL = "https://api.sokhanara.ir/v1/"
    const val API_TIMEOUT = 30L // seconds
    
    // DataStore
    const val PREFERENCES_NAME = "sokhara_preferences"
    
    // File Paths
    const val EXPORT_FOLDER = "Sokhara/Exports"
    const val CACHE_FOLDER = "Sokhara/Cache"
    const val AUDIO_FOLDER = "Sokhara/Audio"
    
    // Speech Stages
    const val STAGE_MOTIVATION = 1
    const val STAGE_CONVICTION = 2
    const val STAGE_EMOTION = 3
    const val STAGE_ACTION = 4
    const val STAGE_RAWZEH = 5
    
    // File Extensions
    const val EXT_PDF = ".pdf"
    const val EXT_DOCX = ".docx"
    const val EXT_PPTX = ".pptx"
    const val EXT_MP3 = ".mp3"
    const val EXT_PNG = ".png"
    
    // Supported Languages
    const val LANG_PERSIAN = "fa"
    const val LANG_ARABIC = "ar"
    
    // TTS Settings
    const val TTS_DEFAULT_SPEED = 1.0f
    const val TTS_DEFAULT_PITCH = 1.0f
    
    // Emotion Analysis
    const val EMOTION_SAMPLE_RATE = 44100
    const val EMOTION_MIN_FREQUENCY = 80.0
    const val EMOTION_MAX_FREQUENCY = 400.0
    
    // Offline Model
    const val OFFLINE_MODEL_NAME = "gemma_2b.onnx"
    const val OFFLINE_MODEL_SIZE_MB = 250
    
    // Network
    const val CACHE_SIZE = 10 * 1024 * 1024L // 10 MB
    const val CACHE_MAX_AGE = 60 * 60 * 24 * 7 // 1 week
    
    // UI
    const val ANIMATION_DURATION = 300
    const val SPLASH_DURATION = 2000L
    
    // Limits
    const val MAX_SOURCE_SIZE_MB = 50
    const val MAX_SPEECH_LENGTH_MINUTES = 60
    const val MAX_AUDIO_LENGTH_MINUTES = 120