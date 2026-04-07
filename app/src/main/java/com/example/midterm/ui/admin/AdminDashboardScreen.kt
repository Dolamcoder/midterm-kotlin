package com.example.midterm.ui.admin

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.midterm.data.model.Role
import com.example.midterm.data.model.User
import androidx.compose.material.icons.*

// ─────────────────────────────────────────────────────────────────────────────
// STATELESS UI COMPONENTS
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun UserCard(
    user: User,
    onEditClick: (User) -> Unit,
    onDeleteClick: (User) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2A3A)),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (user.role == Role.ADMIN) Color(0xFF5E8AB4) else Color(0xFF4CAF82)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.username.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.username,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (user.role == Role.ADMIN) Color(0x335E8AB4) else Color(0x334CAF82)
                ) {
                    Text(
                        text = user.role.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = if (user.role == Role.ADMIN) Color(0xFF5E8AB4) else Color(0xFF4CAF82),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Edit button
            IconButton(onClick = { onEditClick(user) }) {
                Icon(Icons.Default.Edit, contentDescription = "Sửa", tint = Color(0xFF5E8AB4))
            }

            // Delete button
            IconButton(onClick = { onDeleteClick(user) }) {
                Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color(0xFFFF6B6B))
            }
        }
    }
}

@Composable
fun UserFormDialog(
    title: String,
    initialUser: User? = null,
    onDismiss: () -> Unit,
    onConfirm: (username: String, password: String, role: Role, imageUri: Uri?) -> Unit
) {
    var username by remember { mutableStateOf(initialUser?.username ?: "") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(initialUser?.role ?: Role.USER) }
    var passwordVisible by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    // Image picker launcher
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> selectedImageUri = uri }
    )
    
    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                imageLauncher.launch("image/*")
            }
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E2A3A),
        title = {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Avatar upload
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2D3F55))
                        .clickable {
                            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                Manifest.permission.READ_MEDIA_IMAGES
                            } else {
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            }
                            permissionLauncher.launch(permission)
                        }
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else if (!initialUser?.imageUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = java.io.File(initialUser!!.imageUrl),
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
                
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username", color = Color(0xFF8899AA)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF5E8AB4),
                        unfocusedBorderColor = Color(0xFF2D3F55),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFF5E8AB4)
                    ),
                    singleLine = true,
                    readOnly = initialUser != null  // Edit mode: không đổi username
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(if (initialUser != null) "Mật khẩu mới (để trống = giữ nguyên)" else "Mật khẩu", color = Color(0xFF8899AA)) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null, tint = Color(0xFF8899AA)
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF5E8AB4),
                        unfocusedBorderColor = Color(0xFF2D3F55),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFF5E8AB4)
                    ),
                    singleLine = true
                )

                // Role selector
                Text("Phân quyền:", color = Color(0xFF8899AA), fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Role.values().forEach { role ->
                        FilterChip(
                            selected = selectedRole == role,
                            onClick = { selectedRole = role },
                            label = { Text(role.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF5E8AB4),
                                selectedLabelColor = Color.White,
                                labelColor = Color(0xFF8899AA)
                            )
                        )
                    }
                }

                formError?.let {
                    Text(it, color = Color(0xFFFF6B6B), fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (username.isBlank()) {
                        formError = "Username không được để trống"
                        return@Button
                    }
                    if (initialUser == null && password.isBlank()) {
                        formError = "Mật khẩu không được để trống"
                        return@Button
                    }
                    val finalPassword = if (initialUser != null && password.isBlank())
                        initialUser.password else password
                    onConfirm(username, finalPassword, selectedRole, selectedImageUri)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5E8AB4)),
                shape = RoundedCornerShape(8.dp)
            ) { Text("Xác nhận") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Huỷ", color = Color(0xFF8899AA))
            }
        }
    )
}

@Composable
fun DeleteConfirmDialog(
    user: User,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E2A3A),
        title = {
            Text("Xác nhận xóa", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Text(
                "Bạn có chắc muốn xóa tài khoản \"${user.username}\"?\nHành động này không thể hoàn tác.",
                color = Color(0xFF8899AA)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B)),
                shape = RoundedCornerShape(8.dp)
            ) { Text("Xóa") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Huỷ", color = Color(0xFF8899AA))
            }
        }
    )
}

@Composable
fun AdminDashboardContent(
    users: List<User>,
    isLoading: Boolean,
    onAddClick: () -> Unit,
    onEditClick: (User) -> Unit,
    onDeleteClick: (User) -> Unit,
    onLogout: () -> Unit
) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E2A3A))
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Admin Dashboard", color = Color.White,
                            fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("${users.size} tài khoản", color = Color(0xFF8899AA), fontSize = 13.sp)
                    }
                    Row {
                        IconButton(onClick = onAddClick) {
                            Icon(Icons.Default.PersonAdd, contentDescription = "Thêm",
                                tint = Color(0xFF4CAF82), modifier = Modifier.size(28.dp))
                        }
                        IconButton(onClick = onLogout) {
                            Icon(Icons.Default.Logout, contentDescription = "Đăng xuất",
                                tint = Color(0xFFFF6B6B), modifier = Modifier.size(26.dp))
                        }
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF5E8AB4))
                }
            } else if (users.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("😶", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Chưa có tài khoản nào", color = Color(0xFF8899AA))
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    items(users, key = { it.id }) { user ->
                        UserCard(
                            user = user,
                            onEditClick = onEditClick,
                            onDeleteClick = onDeleteClick
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// STATEFUL UI — kết nối ViewModel
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun AdminDashboardScreen(
    onLogout: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionMessage by viewModel.actionMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var showAddDialog by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<User?>(null) }
    var deletingUser by remember { mutableStateOf<User?>(null) }

    // Hiện snackbar khi có actionMessage
    LaunchedEffect(actionMessage) {
        actionMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearActionMessage()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF2D3F55),
                    contentColor = Color.White
                )
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AdminDashboardContent(
                users = (uiState as? AdminUiState.Success)?.users ?: emptyList(),
                isLoading = uiState is AdminUiState.Loading,
                onAddClick = { showAddDialog = true },
                onEditClick = { editingUser = it },
                onDeleteClick = { deletingUser = it },
                onLogout = onLogout
            )
        }
    }

    // Dialog thêm user
    if (showAddDialog) {
        UserFormDialog(
            title = "➕ Thêm tài khoản mới",
            onDismiss = { showAddDialog = false },
            onConfirm = { username, password, role, imageUri ->
                viewModel.createUser(username, password, role, imageUri)
                showAddDialog = false
            }
        )
    }

    // Dialog sửa user
    editingUser?.let { user ->
        UserFormDialog(
            title = "✏️ Sửa tài khoản",
            initialUser = user,
            onDismiss = { editingUser = null },
            onConfirm = { _, password, role, imageUri ->
                viewModel.updateUser(user.copy(password = password, role = role), imageUri)
                editingUser = null
            }
        )
    }

    // Dialog xác nhận xóa
    deletingUser?.let { user ->
        DeleteConfirmDialog(
            user = user,
            onDismiss = { deletingUser = null },
            onConfirm = {
                viewModel.deleteUser(user.id)
                deletingUser = null
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// @Preview
// ─────────────────────────────────────────────────────────────────────────────
@Preview(showBackground = true)
@Composable
fun AdminDashboardContentPreview() {
    val mockUsers = listOf(
        User(id = "1", username = "admin", password = "admin123", role = Role.ADMIN),
        User(id = "2", username = "nguyenvana", password = "pass", role = Role.USER),
        User(id = "3", username = "tranthib", password = "pass", role = Role.USER)
    )
    AdminDashboardContent(
        users = mockUsers,
        isLoading = false,
        onAddClick = {},
        onEditClick = {},
        onDeleteClick = {},
        onLogout = {}
    )
}
