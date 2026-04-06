sh
git checkout feature/study-schedule-An
git checkout develop -- app/src/main/java/com/example/baicuoiki/data/StudySchedule*
git checkout develop -- app/src/main/java/com/example/baicuoiki/ui/ScheduleScreen.kt
git add .
git commit -m "feat: implement detailed study scheduling system"
git push origin feature/study-schedule-An