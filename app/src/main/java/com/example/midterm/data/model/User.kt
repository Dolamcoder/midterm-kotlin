package com.example.midterm.data.model

enum class Role {
    ADMIN, USER
}

data class User(
    val id: String = "",
    val username: String = "",
    val password: String = "",
    val role: Role = Role.USER
) {
    // Firestore yêu cầu constructor không tham số
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "username" to username,
        "password" to password,
        "role" to role.name
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): User = User(
            id = map["id"] as? String ?: "",
            username = map["username"] as? String ?: "",
            password = map["password"] as? String ?: "",
            role = try {
                Role.valueOf(map["role"] as? String ?: "USER")
            } catch (e: Exception) {
                Role.USER
            }
        )
    }
}
