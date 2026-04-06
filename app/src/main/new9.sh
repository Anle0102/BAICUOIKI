sh
git checkout feature/deck-management-An
git checkout develop -- app/src/main/java/com/example/baicuoiki/data/Deck*
git checkout develop -- app/src/main/java/com/example/baicuoiki/data/Flashcard*
git checkout develop -- app/src/main/java/com/example/baicuoiki/ui/DeckScreen.kt
git add .
git commit -m "feat: implement deck and flashcard CRUD with search"
git push origin feature/deck-management-An