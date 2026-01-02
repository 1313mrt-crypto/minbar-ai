package com.sokhanara.app.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Extension Functions
 */

// تبدیل timestamp به تاریخ فارسی
fun Long.toJalaliDate(): String {
    val sdf = SimpleDateFormat("yyyy/MM/dd", Locale("fa", "IR"))
    return sdf.format(Date(this))
}

// تبدیل timestamp به زمان
fun Long.toTime(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale("fa", "IR"))
    return sdf.format(Date(this))
}

// تبدیل تاریخ و زمان کامل
fun Long.toDateTime(): String {
    val sdf = SimpleDateFormat("yyyy/MM/dd - HH:mm", Locale("fa", "IR"))
    return sdf.format(Date(this))
}

// محاسبه مدت زمان به فرمت خوانا
fun Int.toReadableDuration(): String {
    return when {
        this < 1 -> "کمتر از ۱ دقیقه"
        this == 1 -> "۱ دقیقه"
        this < 60 -> "$this دقیقه"
        else -> {
            val hours = this / 60
            val minutes = this % 60
            if (minutes == 0) "$hours ساعت" else "$hours ساعت و $minutes دقیقه"
        }
    }
}

// تبدیل حجم فایل
fun Long.toReadableSize(): String {
    val kb = this / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    
    return when {
        gb >= 1 -> String.format("%.2f GB", gb)
        mb >= 1 -> String.format("%.2f MB", mb)
        kb >= 1 -> String.format("%.2f KB", kb)
        else -> "$this Bytes"
    }
}