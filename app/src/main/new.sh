sh
git checkout develop
git merge feature/auth-An
git merge feature/deck-management-An
git merge feature/study-engine-An
git merge feature/study-schedule-An
# Lưu các file hệ thống còn lại (Database, MainActivity, App)
git add .
git commit -m "feat: finalize project structure and database configuration"
git push origin develop