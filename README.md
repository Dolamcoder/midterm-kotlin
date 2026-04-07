# Lên kế hoạch dự án: Ứng dụng Android Quản lý User (Jetpack Compose + MVVM)

## 1. Mục tiêu dự án
Thiết kế và xây dựng một ứng dụng Android với Jetpack Compose, tập trung vào khả năng quản lý người dùng với phân quyền rõ ràng (Admin/User).

## 2. Mô hình Dữ liệu (Model)
```kotlin
enum class Role { ADMIN, USER }

data class User(
    val id: String = java.util.UUID.randomUUID().toString(),
    val username: String,
    val password: String,
    val role: Role
)
```

## 3. Cấu trúc MVVM và Tách biệt UI Jetpack Compose
Dự án sẽ tuân thủ nghiêm ngặt nguyên tắc **Single Source of Truth** và **Unidirectional Data Flow**. Giao diện sẽ được chia làm hai phần:
- **Stateless UI (Preview-friendly):** Chỉ chứa các hàm `@Composable` nhận dữ liệu (`State`) và các callback (ví dụ: `onLoginClick: (String, String) -> Unit`). Không tự quản lý trạng thái, dùng để check `@Preview`.
- **Stateful UI (Logic-binding):** Gọi đến `ViewModel`, điều hướng (Navigation), và truyền dữ liệu cũng như event trigger xuống Stateless UI.

**Cấu trúc thư mục:**
```text
com.example.usermanagement
├── data
│   ├── model/User.kt
│   └── repository/UserRepository.kt (Interface thao tác dữ liệu)
├── ui
│   ├── auth (Đăng nhập, Đăng ký)
│   │   ├── LoginScreen.kt (Chứa cả Stateless & Stateful con)
│   │   ├── LoginViewModel.kt
│   │   ├── RegisterScreen.kt 
│   │   └── RegisterViewModel.kt
│   ├── admin (Quản lý users CRUD)
│   │   ├── AdminDashboardScreen.kt
│   │   └── AdminViewModel.kt
│   ├── user (Trang cá nhân của user)
│   │   └── UserDashboardScreen.kt
│   └── components (UI dùng chung: Button, TextField, Dialog...)
└── navigation
    └── AppNavigation.kt
```

## 4. Chi tiết các chức năng
- **Đăng nhập & Đăng ký:** Validate input, chuyển hướng thông minh dựa trên Role của User đăng nhập. Account đăng ký mới mặc định sẽ là `USER`.
- **Phân quyền:**
  - `Role.ADMIN`: Có quyền xem danh sách toàn bộ User, thêm User mới (được chọn role), sửa thông tin các User khác, và xóa User.
  - `Role.USER`: Chỉ có quyền vào trang cá nhân, xem và cập nhật thông tin của mình.
- **CRUD Operations (Admin):**
  - Danh sách User: Dùng `LazyColumn`.
  - Thêm / Sửa User: Mở ra một `AlertDialog` hoặc `ModalBottomSheet`.
  - Xóa User: Thêm cảnh báo (Confirm dialog) trước khi xóa.

## 5. Kế hoạch lưu trữ dữ liệu
Hiện tại kế hoạch đang để ngỏ phần `UserRepository`. Các lựa chọn bao gồm:
1. **Mock Data in-memory:** Dữ liệu lưu trữ tạm trên List/Flow trong RAM (dùng để test logic UI nhanh chóng).
2. **Room Database:** Lưu nội bộ offline hoàn toàn.
*Hãy cho mình biết bạn muốn dùng cách lưu nào trước khi chúng ta bắt đầu code nhé!*
