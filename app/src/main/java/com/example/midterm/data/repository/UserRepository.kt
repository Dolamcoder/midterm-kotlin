package com.example.midterm.data.repository

import com.example.midterm.data.model.Role
import com.example.midterm.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class UserRepository {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    // Seed admin account nếu chưa có
    suspend fun seedAdminIfNeeded() {
        val snapshot = usersCollection
            .whereEqualTo("role", Role.ADMIN.name)
            .get().await()
        if (snapshot.isEmpty) {
            val admin = User(
                id = UUID.randomUUID().toString(),
                username = "admin",
                password = "admin123",
                role = Role.ADMIN
            )
            usersCollection.document(admin.id).set(admin.toMap()).await()
        }
    }

    suspend fun login(username: String, password: String): User? {
        val snapshot = usersCollection
            .whereEqualTo("username", username)
            .whereEqualTo("password", password)
            .get().await()
        return if (!snapshot.isEmpty) {
            User.fromMap(snapshot.documents[0].data ?: emptyMap())
        } else null
    }

    suspend fun register(username: String, password: String): Result<User> {
        // Kiểm tra username đã tồn tại chưa
        val existing = usersCollection
            .whereEqualTo("username", username)
            .get().await()
        if (!existing.isEmpty) {
            return Result.failure(Exception("Username đã tồn tại"))
        }

        val newUser = User(
            id = UUID.randomUUID().toString(),
            username = username,
            password = password,
            role = Role.USER
        )
        usersCollection.document(newUser.id).set(newUser.toMap()).await()
        return Result.success(newUser)
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
        val user = User(
            id = UUID.randomUUID().toString(),
            username = username,
            password = password,
            role = role
        )
        usersCollection.document(user.id).set(user.toMap()).await()
        return Result.success(user)
    }

    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            usersCollection.document(user.id).set(user.toMap()).await()
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
