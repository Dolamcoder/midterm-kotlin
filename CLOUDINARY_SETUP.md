# 📸 Hướng dẫn Upload Ảnh Local

## ✅ Cách hoạt động

Ảnh được lưu trực tiếp vào **app's local storage** (không cần Cloudinary):
- 📁 Folder: `app/files/user_images/`
- 📄 Tên file: `img_<UUID>.jpg`
- ✓ Tự động được backing up khi cấu hình

## 🎯 Tính năng

1. **Đăng ký**: Chọn ảnh → lưu local → hiển thị ngay
2. **Dashboard**: Click avatar → chọn ảnh mới → update local

## 🔧 Cấu hình

Không cần cấu hình gì! Chỉ cần:
1. Build project
2. Chạy app
3. Thử chọn ảnh khi đăng ký

## 📍 Vị trí lưu ảnh

```
/data/data/com.example.midterm/files/user_images/
```

## ❌ Loại bỏ Cloudinary

Đã xóa:
- ✗ OkHttp HTTP client
- ✗ Cloudinary API calls
- ✗ JSON parsing

## ✨ Lợi ích

✅ Không cần API key  
✅ Offline hoạt động  
✅ Nhanh hơn  
✅ Đơn giản hơn




