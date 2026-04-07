package com.example.midterm.data.repository

import android.net.Uri
import com.example.midterm.data.model.Role
import com.example.midterm.data.model.User
import com.example.midterm.util.ImageUploadService
import com.example.midterm.utils.PasswordUtils
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class UserRepository {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    suspend fun login(username: String, password: String): Result<User> {
        return try {
            // 1. Tìm user theo username
            val snapshot = usersCollection
                .whereEqualTo("username", username)
                .get()
                .await()

            if (snapshot.isEmpty) {
                return Result.failure(Exception("Sai username hoặc password"))
            }

            val user = User.fromMap(snapshot.documents[0].data ?: emptyMap())

            // 2. Hash password nhập vào
            val hashed = PasswordUtils.hashPassword(password)
            // 3. So sánh
            if (user.password!=hashed) {
                return Result.failure(Exception("Sai username hoặc password"))
            }

            Result.success(user)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun register(username: String, password: String, imageUri: Uri? = null): Result<User> {
        return try {
            if (username.isBlank() || password.length < 6) {
                return Result.failure(Exception("Thông tin không hợp lệ"))
            }

            val existing = usersCollection
                .whereEqualTo("username", username)
                .get()
                .await()

            if (!existing.isEmpty) {
                return Result.failure(Exception("Username đã tồn tại"))
            }

            var imageUrl = ""
            if (imageUri != null) {
                val uploadResult = ImageUploadService.uploadImage(imageUri)
                if (uploadResult.isSuccess) {
                    imageUrl = uploadResult.getOrNull() ?: ""
                } else {
                    return Result.failure(uploadResult.exceptionOrNull() ?: Exception("Lỗi upload image"))
                }
            }

            val hashed = PasswordUtils.hashPassword(password)
            val newUser = User(
                id = UUID.randomUUID().toString(),
                username = username,
                password = hashed,
                role = Role.USER,
                imageUrl = imageUrl
            )

            usersCollection.document(newUser.id)
                .set(newUser.toMap())
                .await()

            Result.success(newUser)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllUsers(): List<User> {
        val snapshot = usersCollection.get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.data?.let { User.fromMap(it) }
        }
    }

    suspend fun createUser(username: String, password: String, role: Role): Result<User> {
        val existing = usersCollection
            .whereEqualTo("username", username)
            .get().await()
        if (!existing.isEmpty) {
            return Result.failure(Exception("Username đã tồn tại"))
        }
        val hashed=PasswordUtils.hashPassword(password)
        val user = User(
            id = UUID.randomUUID().toString(),
            username = username,
            password = hashed,
            role = role
        )
        usersCollection.document(user.id).set(user.toMap()).await()
        return Result.success(user)
    }

    suspend fun updateUser(user: User, imageUri: Uri? = null): Result<Unit> {
        return try {
            var updatedUser = user
            
            // Mã hóa mật khẩu nếu nó không phải là hash
            if (updatedUser.password.isNotEmpty() && !updatedUser.password.startsWith("\$2a\$") && !updatedUser.password.startsWith("\$2b\$")) {
                updatedUser = updatedUser.copy(password = PasswordUtils.hashPassword(updatedUser.password))
            }
            
            // Upload image nếu có
            if (imageUri != null) {
                val uploadResult = ImageUploadService.uploadImage(imageUri)
                if (uploadResult.isSuccess) {
                    updatedUser = updatedUser.copy(imageUrl = uploadResult.getOrNull() ?: "")
                } else {
                    return Result.failure(uploadResult.exceptionOrNull() ?: Exception("Lỗi upload image"))
                }
            }

            usersCollection.document(updatedUser.id).set(updatedUser.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            usersCollection.document(userId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}