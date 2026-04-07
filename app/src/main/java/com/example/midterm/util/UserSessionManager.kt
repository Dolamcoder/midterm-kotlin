package com.example.midterm.util

import com.example.midterm.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object UserSessionManager {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }

    fun getCurrentUser(): User? = _currentUser.value

    fun logout() {
        _currentUser.value = null
    }
}

