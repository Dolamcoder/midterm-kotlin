package com.example.midterm.ui.user

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.midterm.data.model.Role
import com.example.midterm.data.model.User
import com.example.midterm.data.repository.UserRepository
import com.example.midterm.util.UserSessionManager
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────────
// STATELESS UI — preview-friendly
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun UserDashboardContent(
    user: User?,
    onLogout: () -> Unit,
    onImageUpdate: (Uri) -> Unit = {}
) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E2A3A))
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .align(Alignment.TopStart)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Trang cá nhân",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                IconButton(onClick = onLogout) {
                    Icon(
                        Icons.Default.Logout,
                        contentDescription = "Đăng xuất",
                        tint = Color(0xFFFF6B6B),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

        // Body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Avatar lớn - có thể click để update
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF82))
                    .clickable { onImageUpdate(Uri.EMPTY) },
                contentAlignment = Alignment.Center
            ) {
                if (!user?.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = java.io.File(user!!.imageUrl),
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = user?.username?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = user?.username ?: "Unknown",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0x334CAF82)
            ) {
                Text(
                    text = "🧑 USER",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                    color = Color(0xFF4CAF82),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2A3A)),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Thông tin tài khoản", color = Color.White,
                        fontWeight = FontWeight.SemiBold, fontSize = 16.sp)

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFF2D3F55))
                    Spacer(modifier = Modifier.height(16.dp))

                    InfoRow(label = "Tên đăng nhập", value = user?.username ?: "-")
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoRow(label = "Vai trò", value = user?.role?.name ?: "-")
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoRow(label = "ID tài khoản", value = user?.id?.take(8)?.plus("...") ?: "-")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Thông báo quyền
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x1A4CAF82))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF4CAF82))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Tài khoản User: Bạn có thể xem thông tin cá nhân của mình.",
                        color = Color(0xFF8899AA),
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0xFF8899AA), fontSize = 14.sp)
        Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// STATEFUL UI — đơn giản, user được truyền vào từ Session hoặc NavArgs
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun UserDashboardScreen(onLogout: () -> Unit) {
    val currentUser by UserSessionManager.currentUser.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { UserRepository() }
    var isUpdating by remember { mutableStateOf(false) }
    
    // Image picker launcher (định nghĩa trước)
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null && currentUser != null) {
                isUpdating = true
                coroutineScope.launch {
                    val result = repository.updateUser(currentUser!!, uri)
                    if (result.isSuccess) {
                        // Reload user từ session để lấy imageUrl mới
                        val reloadedUser = currentUser!!.copy(imageUrl = currentUser!!.imageUrl)
                        UserSessionManager.setCurrentUser(reloadedUser)
                    }
                    isUpdating = false
                }
            }
        }
    )
    
    // Permission request handler
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                imageLauncher.launch("image/*")
            }
        }
    )
    
    UserDashboardContent(
        user = currentUser,
        onLogout = onLogout,
        onImageUpdate = {
            if (!isUpdating) {
                val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_IMAGES
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }
                permissionLauncher.launch(permission)
            }
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// @Preview
// ─────────────────────────────────────────────────────────────────────────────
@Preview(showBackground = true)
@Composable
fun UserDashboardContentPreview() {
    UserDashboardContent(
        user = User(
            id = "abc-123-xyz",
            username = "nguyenvana",
            password = "hidden",
            role = Role.USER
        ),
        onLogout = {}
    )
}
