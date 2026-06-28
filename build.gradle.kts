plugins {
    // مطابق با استاندارد جدید سایت اندروید
    id("com.android.application") version "9.2.0" apply false
    id("com.android.library") version "9.2.0" apply false
    id("org.jetbrains.kotlin.android") version "2.3.21" apply false

    // پلاگین‌های جانبی (باید نسخه‌های سازگار با کاتلین 2.x باشند)
    // توجه: kapt در کاتلین 2.0+ توصیه نمی‌شود، اما اگر همچنان استفاده می‌کنید:
    id("org.jetbrains.kotlin.kapt") version "2.3.21" apply false
    
    // هیلت و KSP سازگار با کاتلین 2.x
    id("com.google.dagger.hilt.android") version "2.52" apply false
    id("com.google.devtools.ksp") version "2.3.21-1.0.29" apply false 
}
