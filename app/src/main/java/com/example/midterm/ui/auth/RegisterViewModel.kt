package com.example.midterm.ui.auth

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.midterm.data.model.User
import com.example.midterm.data.repository.AuthRepository
import com.example.midterm.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    data class Success(val user: User) : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}

class RegisterViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun register(username: String, password: String, confirmPassword: String, imageUri: Uri? = null) {
        when {
            username.isBlank() || password.isBlank() -> {
                _uiState.value = RegisterUiState.Error("Vui lòng điền đầy đủ thông tin")
            }
            username.length < 3 -> {
                _uiState.value = RegisterUiState.Error("Tên đăng nhập phải có ít nhất 3 ký tự")
            }
            password.length < 6 -> {
                _uiState.value = RegisterUiState.Error("Mật khẩu phải có ít nhất 6 ký tự")
            }
            password != confirmPassword -> {
                _uiState.value = RegisterUiState.Error("Mật khẩu xác nhận không khớp")
            }
            else -> {
                viewModelScope.launch {
                    _uiState.value = RegisterUiState.Loading
                    try {
                        val result = repository.register(username.trim(), password.trim(), imageUri)
                        result.fold(
                            onSuccess = {_uiState.value = RegisterUiState.Success(it) },
                            onFailure = { _uiState.value = RegisterUiState.Error(it.message ?: "Đăng ký thất bại") }
                        )
                    } catch (e: Exception) {
                        _uiState.value = RegisterUiState.Error("Lỗi kết nối: ${e.message}")
                    }
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = RegisterUiState.Idle
    }
}
