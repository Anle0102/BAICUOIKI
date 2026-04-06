package com.example.baicuoiki.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.baicuoiki.data.Flashcard
import com.example.baicuoiki.util.TtsHelper

@Composable
fun ReviewScreen(
    viewModel: ReviewViewModel = hiltViewModel(),
    onFinish: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val ttsHelper = remember { TtsHelper(context) }

    DisposableEffect(Unit) {
        onDispose { ttsHelper.shutdown() }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val state = uiState) {
            is ReviewUiState.Loading -> CircularProgressIndicator()
            is ReviewUiState.Empty -> {
                Text("Không có thẻ nào cần ôn tập hôm nay!", fontSize = 18.sp)
            }
            is ReviewUiState.Finished -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Chúc mừng! Bạn đã hoàn thành bài học.", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onFinish) { Text("Quay lại") }
                }
            }
            is ReviewUiState.Success -> {
                FlashcardContent(
                    card = state.card,
                    isFlipped = state.isFlipped,
                    onFlip = { 
                        viewModel.flipCard()
                        if (!state.isFlipped) ttsHelper.speak(state.card.back)
                    },
                    onRate = { quality -> viewModel.rateCard(quality) }
                )
            }
        }
    }
}

@Composable
fun FlashcardContent(
    card: Flashcard,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onRate: (Int) -> Unit
) {
    val rotation by animateFloatAsState(targetValue = if (isFlipped) 180f else 0f)

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                }
                .clickable { onFlip() },
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (rotation <= 90f) {
                    Text(text = card.front, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                } else {
                    Text(
                        text = card.back,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.graphicsLayer { rotationY = 180f }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (isFlipped) {
            Text("Bạn nhớ thẻ này thế nào?", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                (0..5).forEach { quality ->
                    Button(
                        onClick = { onRate(quality) },
                        contentPadding = PaddingValues(4.dp),
                        modifier = Modifier.size(45.dp)
                    ) {
                        Text(quality.toString())
                    }
                }
            }
        } else {
            Text("Chạm vào thẻ để xem đáp án", color = MaterialTheme.colorScheme.secondary)
        }
    }
}
