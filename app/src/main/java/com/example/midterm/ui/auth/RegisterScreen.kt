package com.example.midterm.ui.auth

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import coil.compose.AsyncImage
import com.example.midterm.util.UserSessionManager

// ─────────────────────────────────────────────────────────────────────────────
// STATELESS UI — preview-friendly
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun RegisterContent(
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    imageUri: Uri?,
    onImageSelected: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F3460), Color(0xFF16213E), Color(0xFF1A1A2E))
    )
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "✨ Tạo tài khoản",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Đăng ký để bắt đầu sử dụng",
                    fontSize = 13.sp,
                    color = Color(0xFF8899AA),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Avatar upload
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2D3F55))
                        .clickable { onImageSelected() },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Image,
                                contentDescription = null,
                                tint = Color(0xFF8899AA),
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                "Chọn ảnh",
                                fontSize = 10.sp,
                                color = Color(0xFF8899AA)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Username
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

                // Password
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
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null, tint = Color(0xFF8899AA)
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

                // Confirm Password
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = { Text("Xác nhận mật khẩu", color = Color(0xFF8899AA)) },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF5E8AB4))
                    },
                    trailingIcon = {
                        IconButton(onClick = { confirmVisible = !confirmVisible }) {
                            Icon(
                                if (confirmVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null, tint = Color(0xFF8899AA)
                            )
                        }
                    },
                    visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
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

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFFF6B6B),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = onRegisterClick,
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF82))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Đăng ký", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Đã có tài khoản? ", color = Color(0xFF8899AA), fontSize = 14.sp)
                    Text(
                        text = "Đăng nhập",
                        color = Color(0xFF5E8AB4),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onLoginClick() }
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// STATEFUL UI — kết nối ViewModel
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var imageUri: Uri? by remember { mutableStateOf(null) }
    
    // Image picker launcher (định nghĩa trước)
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> imageUri = uri }
    )
    
    // Permission request handler
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Permission granted, launch image picker
                imageLauncher.launch("image/*")
            }
        }
    )

    LaunchedEffect(uiState) {
        if (uiState is RegisterUiState.Success) {
            val user = (uiState as RegisterUiState.Success).user
            UserSessionManager.setCurrentUser(user)
            onRegisterSuccess()
            viewModel.resetState()
        }
    }

    RegisterContent(
        username = username,
        onUsernameChange = { username = it },
        password = password,
        onPasswordChange = { password = it },
        confirmPassword = confirmPassword,
        onConfirmPasswordChange = { confirmPassword = it },
        imageUri = imageUri,
        onImageSelected = {
            // Request permission if needed
            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
            permissionLauncher.launch(permission)
        },
        isLoading = uiState is RegisterUiState.Loading,
        errorMessage = (uiState as? RegisterUiState.Error)?.message,
        onRegisterClick = { viewModel.register(username, password, confirmPassword, imageUri) },
        onLoginClick = onNavigateToLogin
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// @Preview
// ─────────────────────────────────────────────────────────────────────────────
@Preview(showBackground = true)
@Composable
fun RegisterContentPreview() {
    RegisterContent(
        username = "newuser",
        onUsernameChange = {},
        password = "",
        onPasswordChange = {},
        confirmPassword = "",
        onConfirmPasswordChange = {},
        imageUri = null,
        onImageSelected = {},
        isLoading = false,
        errorMessage = null,
        onRegisterClick = {},
        onLoginClick = {}
    )
}
