package com.sokhanara.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Project Entity
 * جدول پروژه‌ها (برای سازماندهی بهتر)
 */
@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String?,
    val speechIds: List<String>, // لیست ID سخنرانی‌ها
    val createdAt: Long,
    val updatedAt: Long,
    val color: String? // برای رنگ‌بندی
)