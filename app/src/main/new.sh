sh
git checkout feature/auth-An
# Lấy các file liên quan từ develop sang
git checkout develop -- app/src/main/java/com/example/baicuoiki/data/User*
git checkout develop -- app/src/main/java/com/example/baicuoiki/ui/LoginScreen.kt
git checkout develop -- app/src/main/java/com/example/baicuoiki/ui/RegisterScreen.kt
git checkout develop -- app/src/main/java/com/example/baicuoiki/ui/AuthViewModel.kt
git checkout develop -- app/src/main/java/com/example/baicuoiki/ui/ProfileScreen.kt
# Lưu và đẩy lên
git add .
git commit -m "feat: implement authentication system and user profile"
git push origin feature/auth-An