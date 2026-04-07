package com.example.midterm.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.midterm.data.model.Role
import com.example.midterm.util.UserSessionManager

// ─────────────────────────────────────────────────────────────────────────────
// STATELESS UI — preview-friendly, nhận data + callbacks từ ngoài
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun LoginContent(
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))
    )
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2A3A)),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Text(
                    text = "Chào mừng",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Đăng nhập để tiếp tục",
                    fontSize = 14.sp,
                    color = Color(0xFF8899AA),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Username field
                OutlinedTextField(
                    value = username,
                    onValueChange = onUsernameChange,
                    label = { Text("Tên đăng nhập", color = Color(0xFF8899AA)) },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF5E8AB4))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF5E8AB4),
                        unfocusedBorderColor = Color(0xFF2D3F55),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFF5E8AB4)
                    ),
                    singleLine = true
                )

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text("Mật khẩu", color = Color(0xFF8899AA)) },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF5E8AB4))
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = Color(0xFF8899AA)
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF5E8AB4),
                        unfocusedBorderColor = Color(0xFF2D3F55),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFF5E8AB4)
                    ),
                    singleLine = true
                )

                // Error message
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFFF6B6B),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // Login button
                Button(
                    onClick = onLoginClick,
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5E8AB4))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Đăng nhập", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Register link
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Chưa có tài khoản? ", color = Color(0xFF8899AA), fontSize = 14.sp)
                    Text(
                        text = "Đăng ký",
                        color = Color(0xFF5E8AB4),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onRegisterClick() }
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// STATEFUL UI — kết nối ViewModel + Navigation
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: (Role) -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Xử lý navigation khi login thành công
    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            val user = (uiState as LoginUiState.Success).user
            UserSessionManager.setCurrentUser(user)
            onLoginSuccess(user.role)
            viewModel.resetState()
        }
    }

    LoginContent(
        username = username,
        onUsernameChange = { username = it },
        password = password,
        onPasswordChange = { password = it },
        isLoading = uiState is LoginUiState.Loading,
        errorMessage = (uiState as? LoginUiState.Error)?.message,
        onLoginClick = { viewModel.login(username, password) },
        onRegisterClick = onNavigateToRegister
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// @Preview — chỉ dùng Stateless UI
// ─────────────────────────────────────────────────────────────────────────────
@Preview(showBackground = true)
@Composable
fun LoginContentPreview() {
    LoginContent(
        username = "admin",
        onUsernameChange = {},
        password = "",
        onPasswordChange = {},
        isLoading = false,
        errorMessage = null,
        onLoginClick = {},
        onRegisterClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun LoginContentErrorPreview() {
    LoginContent(
        username = "admin",
        onUsernameChange = {},
        password = "wrong",
        onPasswordChange = {},
        isLoading = false,
        errorMessage = "Sai tên đăng nhập hoặc mật khẩu",
        onLoginClick = {},
        onRegisterClick = {}
    )
}
