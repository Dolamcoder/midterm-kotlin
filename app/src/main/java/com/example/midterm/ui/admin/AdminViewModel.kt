package com.example.midterm.ui.admin

import android.net.Uri
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

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

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

    fun createUser(username: String, password: String, role: Role, imageUri: Uri? = null) {
        viewModelScope.launch {
            try {
                _isUploading.value = true
                var imageUrl = ""
                
                // Nếu có ảnh, upload trước
                if (imageUri != null) {
                    val tempId = java.util.UUID.randomUUID().toString()
                    val uploadResult = repository.uploadImage(imageUri, tempId)
                    uploadResult.fold(
                        onSuccess = { url -> imageUrl = url },
                        onFailure = { 
                            _actionMessage.value = "Upload ảnh thất bại: ${it.message}"
                            _isUploading.value = false
                            return@launch
                        }
                    )
                }
                
                val result = repository.createUser(username.trim(), password.trim(), role, imageUrl)
                result.fold(
                    onSuccess = {
                        _actionMessage.value = "Tạo tài khoản thành công"
                        loadUsers()
                    },
                    onFailure = { _actionMessage.value = it.message }
                )
            } catch (e: Exception) {
                _actionMessage.value = "Lỗi: ${e.message}"
            } finally {
                _isUploading.value = false
            }
        }
    }

    fun updateUser(user: User, imageUri: Uri? = null) {
        viewModelScope.launch {
            try {
                _isUploading.value = true
                var updatedUser = user
                
                // Nếu có ảnh mới, upload
                if (imageUri != null) {
                    val uploadResult = repository.uploadImage(imageUri, user.id)
                    uploadResult.fold(
                        onSuccess = { url -> updatedUser = user.copy(imageUrl = url) },
                        onFailure = { 
                            _actionMessage.value = "Upload ảnh thất bại: ${it.message}"
                            _isUploading.value = false
                            return@launch
                        }
                    )
                }
                
                val result = repository.updateUser(updatedUser)
                result.fold(
                    onSuccess = {
                        _actionMessage.value = "Cập nhật thành công"
                        loadUsers()
                    },
                    onFailure = { _actionMessage.value = it.message }
                )
            } catch (e: Exception) {
                _actionMessage.value = "Lỗi: ${e.message}"
            } finally {
                _isUploading.value = false
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
