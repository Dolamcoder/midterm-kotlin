package com.example.midterm.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.midterm.data.model.Role
import com.example.midterm.data.model.User
import com.example.midterm.data.repository.AuthRepository
import com.example.midterm.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val user: User) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Vui lòng điền đầy đủ thông tin")
            return
        }
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val result = repository.login(username.trim(), password.trim())
                result.fold(
                    onSuccess = { user ->
                        _uiState.value = LoginUiState.Success(user)
                    },
                    onFailure = {
                        _uiState.value = LoginUiState.Error("Sai tên đăng nhập hoặc mật khẩu")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("Lỗi kết nối: ${e.message}")
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}
