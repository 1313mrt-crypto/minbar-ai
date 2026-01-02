package com.sokhanara.app.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

/**
 * File Utilities
 */
object FileUtil {
    
    fun getExportDirectory(context: Context): File {
        val dir = File(context.getExternalFilesDir(null), Constants.EXPORT_FOLDER)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    fun getAudioDirectory(context: Context): File {
        val dir = File(context.getExternalFilesDir(null), Constants.AUDIO_FOLDER)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    fun getCacheDirectory(context: Context): File {
        val dir = File(context.cacheDir, Constants.CACHE_FOLDER)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    fun copyUriToFile(context: Context, uri: Uri, destFile: File): Boolean {
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun deleteFile(path: String): Boolean {
        return try {
            val file = File(path)
            file.delete()
        } catch (e: Exception) {
            false
        }
    }
    
    fun getFileExtension(filename: String): String {
        return filename.substringAfterLast('.', "")
    }
}