package com.example.midterm.util

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

@Suppress("unused", "MemberVisibilityCanBePrivate")
object ImageUploadService {
    
    private var context: Context? = null
    private const val IMAGES_DIR = "user_images"
    
    fun init(context: Context) {
        this.context = context
        // Tạo folder cho ảnh
        val imagesFolder = File(context.filesDir, IMAGES_DIR)
        if (!imagesFolder.exists()) {
            imagesFolder.mkdirs()
        }
    }
    
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun uploadImage(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val ctx = context ?: return@withContext Result.failure(Exception("Context not initialized"))
            
            // Đọc file từ URI
            val inputStream = ctx.contentResolver.openInputStream(uri)
                ?: return@withContext Result.failure(Exception("Không thể mở file"))
            
            // Thêm timestamp để tạo filename mới mỗi lần, tránh cache
            val fileName = "img_${System.currentTimeMillis()}_${UUID.randomUUID()}.jpg"
            val imageFile = File(ctx.filesDir, "$IMAGES_DIR/$fileName")
            
            inputStream.use { input ->
                imageFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            
            // Trả về path file
            Result.success(imageFile.absolutePath)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

