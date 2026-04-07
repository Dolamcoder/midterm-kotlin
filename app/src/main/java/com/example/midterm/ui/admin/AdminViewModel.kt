package com.example.midterm.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.midterm.data.model.Role
import com.example.midterm.data.model.User
import com.example.midterm.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AdminUiState {
    object Loading : AdminUiState()
    data class Success(val users: List<User>) : AdminUiState()
    data class Error(val message: String) : AdminUiState()
}

class AdminViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _uiState = MutableStateFlow<AdminUiState>(AdminUiState.Loading)
    val uiState: StateFlow<AdminUiState> = _uiState

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = AdminUiState.Loading
            try {
                val users = repository.getAllUsers()
                _uiState.value = AdminUiState.Success(users)
            } catch (e: Exception) {
                _uiState.value = AdminUiState.Error("Không thể tải danh sách: ${e.message}")
            }
        }
    }

    fun createUser(username: String, password: String, role: Role) {
        viewModelScope.launch {
            try {
                val result = repository.createUser(username.trim(), password.trim(), role)
                result.fold(
                    onSuccess = {
                        _actionMessage.value = "Tạo tài khoản thành công"
                        loadUsers()
                    },
                    onFailure = { _actionMessage.value = it.message }
                )
            } catch (e: Exception) {
                _actionMessage.value = "Lỗi: ${e.message}"
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            try {
                val result = repository.updateUser(user)
                result.fold(
                    onSuccess = {
                        _actionMessage.value = "Cập nhật thành công"
                        loadUsers()
                    },
                    onFailure = { _actionMessage.value = it.message }
                )
            } catch (e: Exception) {
                _actionMessage.value = "Lỗi: ${e.message}"
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            try {
                val result = repository.deleteUser(userId)
                result.fold(
                    onSuccess = {
                        _actionMessage.value = "Xóa tài khoản thành công"
                        loadUsers()
                    },
                    onFailure = { _actionMessage.value = it.message }
                )
            } catch (e: Exception) {
                _actionMessage.value = "Lỗi: ${e.message}"
            }
        }
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }
}
