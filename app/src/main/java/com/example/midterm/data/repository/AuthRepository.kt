package com.example.midterm.data.repository

import com.example.midterm.data.model.Role
import com.example.midterm.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val userRef = FirebaseFirestore.getInstance().collection("users")

    suspend fun register(email: String, password: String): Result<User> {
        return try {
            val result = auth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val firebaseUser = result.user
                ?: return Result.failure(Exception("Không tạo được user"))
            val user = User(
                id = firebaseUser.uid,
                username = email,
                password = "",
                role = Role.USER
            )
            userRef.document(user.id).set(user).await()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = auth
                .signInWithEmailAndPassword(email, password)
                .await()

            val uid = result.user!!.uid

            val snapshot = userRef.document(uid).get().await()
            val user = snapshot.toObject(User::class.java)!!
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun getCurrentUser(): Result<User> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("Chưa đăng nhập"))

            val snapshot = userRef.document(uid).get().await()
            val user = snapshot.toObject(User::class.java)!!

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}