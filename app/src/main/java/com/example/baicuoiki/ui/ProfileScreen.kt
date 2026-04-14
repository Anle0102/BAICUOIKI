package com.example.baicuoiki.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    flashcardViewModel: FlashcardViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val dailyStats by flashcardViewModel.getStudyStatsPastWeek().collectAsState(initial = emptyList())
    val dailyTimeStats by flashcardViewModel.getStudyTimePastWeek().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trang cá nhân") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = currentUser?.fullName ?: "Người dùng",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // THỐNG KÊ SỐ LƯỢNG THẺ
            StudyStatsCard(
                title = "Thẻ đã ôn tập (7 ngày)",
                icon = Icons.Default.BarChart,
                stats = dailyStats.map { it.toLong() },
                unit = "thẻ",
                label = "Số lượng thẻ đã ôn tập mỗi ngày"
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            // THỐNG KÊ THỜI GIAN HỌC
            StudyStatsCard(
                title = "Thời gian học (7 ngày)",
                icon = Icons.Default.Timer,
                stats = dailyTimeStats,
                unit = "phút",
                label = "Tổng thời gian học mỗi ngày (phút)",
                isTime = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Thông tin tài khoản", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Họ tên: ${currentUser?.fullName}")
                    Text(text = "Tên đăng nhập: ${currentUser?.username}")
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    authViewModel.logout()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Đăng xuất")
            }
        }
    }
}

@Composable
fun StudyStatsCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    stats: List<Long>,
    unit: String,
    label: String,
    isTime: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                val displayStats = (List(7 - stats.size) { 0L } + stats).takeLast(7)
                val maxVal = (displayStats.maxOrNull() ?: 1L).coerceAtLeast(1L)

                displayStats.forEach { value ->
                    val displayValue = if (isTime) TimeUnit.MILLISECONDS.toMinutes(value) else value
                    val barHeight = (value.toFloat() / maxVal * 80).coerceAtLeast(2f).dp
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = displayValue.toString(), fontSize = 9.sp)
                        Box(
                            modifier = Modifier
                                .width(18.dp)
                                .height(barHeight)
                                .background(
                                    color = if (value > 0) MaterialTheme.colorScheme.primary else Color.LightGray,
                                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
                        )
                    }
                }
            }
            Text(
                text = label,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp
            )
        }
    }
}
