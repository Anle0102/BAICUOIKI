sh
git checkout feature/study-engine-An
git checkout develop -- app/src/main/java/com/example/baicuoiki/util/SM2Algorithm.kt
git checkout develop -- app/src/main/java/com/example/baicuoiki/data/StudyLog*
git checkout develop -- app/src/main/java/com/example/baicuoiki/ui/StudyScreen.kt
git checkout develop -- app/src/main/java/com/example/baicuoiki/ui/FlashcardViewModel.kt
git add .
git commit -m "feat: implement SM-2 algorithm and 3D flip card study mode"
git push origin feature/study-engine-An