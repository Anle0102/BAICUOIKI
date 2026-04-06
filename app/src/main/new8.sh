sh
# 1. Chuyển sang nhánh auth
git checkout feature/auth-An

# 2. Lấy code auth từ develop sang (vì bạn đang để code ở develop)
git checkout develop -- app/src/main/java/com/example/baicuoiki/data/User*
git checkout develop -- app/src/main/java/com/example/baicuoiki/ui/LoginScreen.kt
git checkout develop -- app/src/main/java/com/example/baicuoiki/ui/RegisterScreen.kt
git checkout develop -- app/src/main/java/com/example/baicuoiki/ui/AuthViewModel.kt
git checkout develop -- app/src/main/java/com/example/baicuoiki/ui/ProfileScreen.kt

# 3. Lưu và đẩy lên mạng
git add .
git commit -m "feat: implement authentication, registration and profile screen"
git push origin feature/auth-An