package com.example.baicuoiki.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.baicuoiki.data.Flashcard
import com.example.baicuoiki.util.TtsHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(
    deckId: Long,
    viewModel: FlashcardViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val ttsHelper = remember { TtsHelper(context) }
    
    val cards by viewModel.getFlashcardsForDeck(deckId).collectAsState(initial = emptyList())
    
    var isStudyMode by remember { mutableStateOf(false) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    
    var showAddCardDialog by remember { mutableStateOf(false) }
    var showEditCardDialog by remember { mutableStateOf<Flashcard?>(null) }
    
    var cardFront by remember { mutableStateOf(TextFieldValue("")) }
    var cardBack by remember { mutableStateOf(TextFieldValue("")) }

    // Quản lý session học tập và ghi nhận thời gian
    LaunchedEffect(isStudyMode) {
        if (isStudyMode && cards.isNotEmpty()) {
            viewModel.startStudySession()
        } else if (!isStudyMode) {
            viewModel.endStudySession()
        }
    }

    // Đảm bảo kết thúc session khi thoát màn hình
    DisposableEffect(Unit) {
        onDispose { 
            if (isStudyMode) {
                viewModel.endStudySession()
            }
            ttsHelper.shutdown() 
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isStudyMode) "Đang học" else "Quản lý thẻ") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                if (!isStudyMode) {
                    // Nút Thêm Thẻ Mới
                    SmallFloatingActionButton(
                        onClick = {
                            cardFront = TextFieldValue("")
                            cardBack = TextFieldValue("")
                            showAddCardDialog = true
                        },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Thêm thẻ")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Nút Bắt đầu / Kết thúc học
                if (cards.isNotEmpty() || isStudyMode) {
                    ExtendedFloatingActionButton(
                        onClick = { 
                            isStudyMode = !isStudyMode
                            if (!isStudyMode) {
                                currentIndex = 0
                                isFlipped = false
                            }
                        },
                        containerColor = if (isStudyMode) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        contentColor = if (isStudyMode) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary,
                        icon = {
                            Icon(
                                if (isStudyMode) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = null
                            )
                        },
                        text = {
                            Text(if (isStudyMode) "Kết thúc học" else "Bắt đầu học")
                        }
                    )
                }
            }
        }
    ) { padding ->
        if (isStudyMode) {
            if (cards.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Chưa có thẻ nào để học!")
                }
            } else {
                StudyContent(
                    cards = cards,
                    currentIndex = currentIndex,
                    isFlipped = isFlipped,
                    onFlip = { isFlipped = !isFlipped },
                    onSpeak = { text -> ttsHelper.speak(text) },
                    onRate = { quality ->
                        viewModel.updateFlashcardReview(cards[currentIndex], quality)
                        if (currentIndex < cards.size - 1) {
                            currentIndex++
                            isFlipped = false
                        } else {
                            isStudyMode = false // Tự động kết thúc khi hết thẻ
                        }
                    },
                    modifier = Modifier.padding(padding)
                )
            }
        } else {
            CardListContent(
                cards = cards,
                onEdit = { card ->
                    cardFront = TextFieldValue(card.front)
                    cardBack = TextFieldValue(card.back)
                    showEditCardDialog = card
                },
                onDelete = { viewModel.deleteFlashcard(it) },
                modifier = Modifier.padding(padding)
            )
        }

        // Dialog Thêm Thẻ
        if (showAddCardDialog) {
            FlashcardDialog(
                title = "Thêm thẻ ghi nhớ mới",
                front = cardFront,
                back = cardBack,
                onFrontChange = { cardFront = it },
                onBackChange = { cardBack = it },
                onConfirm = {
                    if (cardFront.text.isNotBlank() && cardBack.text.isNotBlank()) {
                        viewModel.addFlashcard(deckId, cardFront.text, cardBack.text)
                        showAddCardDialog = false
                    }
                },
                onDismiss = { showAddCardDialog = false }
            )
        }

        // Dialog Sửa Thẻ
        showEditCardDialog?.let { card ->
            FlashcardDialog(
                title = "Sửa thẻ ghi nhớ",
                front = cardFront,
                back = cardBack,
                onFrontChange = { cardFront = it },
                onBackChange = { cardBack = it },
                onConfirm = {
                    if (cardFront.text.isNotBlank() && cardBack.text.isNotBlank()) {
                        viewModel.updateFlashcard(card.copy(front = cardFront.text, back = cardBack.text))
                        showEditCardDialog = null
                    }
                },
                onDismiss = { showEditCardDialog = null }
            )
        }
    }
}

@Composable
fun StudyContent(
    cards: List<Flashcard>,
    currentIndex: Int,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onSpeak: (String) -> Unit,
    onRate: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val currentCard = cards.getOrNull(currentIndex)
        if (currentCard != null) {
            Text(text = "Thẻ ${currentIndex + 1} / ${cards.size}")
            Spacer(modifier = Modifier.height(16.dp))

            FlashcardItem(
                card = currentCard,
                isFlipped = isFlipped,
                onFlip = onFlip,
                onSpeak = { onSpeak(currentCard.front) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isFlipped) {
                Text("Đánh giá độ khó:")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (quality in 0..5) {
                        Button(
                            onClick = { onRate(quality) },
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            Text(quality.toString())
                        }
                    }
                }
            } else {
                Button(onClick = onFlip) {
                    Text("Xem đáp án")
                }
            }
        }
    }
}

@Composable
fun CardListContent(
    cards: List<Flashcard>,
    onEdit: (Flashcard) -> Unit,
    onDelete: (Flashcard) -> Unit,
    modifier: Modifier = Modifier
) {
    if (cards.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Chưa có thẻ nào. Nhấn nút + để thêm!")
        }
    } else {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            items(cards) { card ->
                var isVisible by remember { mutableStateOf(false) }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { isVisible = !isVisible }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Câu hỏi: ${card.front}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            if (isVisible) {
                                Text(text = "Đáp án: ${card.back}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                            } else {
                                Text(text = "Chạm để xem đáp án", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                        IconButton(onClick = { isVisible = !isVisible }) {
                            Icon(
                                imageVector = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = { onEdit(card) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Sửa")
                        }
                        IconButton(onClick = { onDelete(card) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Xóa")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlashcardDialog(
    title: String,
    front: TextFieldValue,
    back: TextFieldValue,
    onFrontChange: (TextFieldValue) -> Unit,
    onBackChange: (TextFieldValue) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = front,
                    onValueChange = onFrontChange,
                    label = { Text("Mặt trước (Câu hỏi)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = back,
                    onValueChange = onBackChange,
                    label = { Text("Mặt sau (Đáp án)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) { Text("Lưu") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

@Composable
fun FlashcardItem(
    card: Flashcard,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onSpeak: () -> Unit
) {
    val rotation by animateFloatAsState(targetValue = if (isFlipped) 180f else 0f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable { onFlip() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = card.front,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    IconButton(onClick = onSpeak) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Speak")
                    }
                }
            } else {
                Text(
                    text = card.back,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.graphicsLayer { rotationY = 180f }
                )
            }
        }
    }
}
