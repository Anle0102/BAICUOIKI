package com.example.baicuoiki.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: FlashcardViewModel,
    onNavigateToDecks: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val decks by viewModel.decks.collectAsState()
    val reviewCards by viewModel.getCardsToReviewToday().collectAsState(initial = emptyList())
    val schedules by viewModel.schedules.collectAsState()
    
    // Lọc lịch học sắp tới
    val upcomingSchedules = schedules.filter { it.scheduledTime > System.currentTimeMillis() }
        .sortedBy { it.scheduledTime }
        .take(2)

    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("Bảng điều khiển") },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Trang cá nhân")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Đăng xuất")
                    }
                }
            ) 
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Chào mừng bạn quay lại!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            // --- NÚT ĐẶT LỊCH HỌC CHÍNH ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                onClick = onNavigateToSchedule,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EditCalendar, 
                        contentDescription = null, 
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Lên lịch học bài mới", 
                            style = MaterialTheme.typography.titleLarge, 
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Đặt ngày, giờ và chọn bộ thẻ", 
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Thống kê nhanh
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard("Bộ thẻ", decks.size.toString(), Icons.Default.Folder, Modifier.weight(1f))
                StatCard("Cần ôn", reviewCards.size.toString(), Icons.Default.HistoryEdu, Modifier.weight(1f))
            }

            // Danh sách lịch học đã đặt
            Text(text = "Lịch học sắp tới", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            if (upcomingSchedules.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = "Chưa có lịch hẹn học nào. Nhấn vào nút xanh phía trên để đặt lịch ngay!",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                upcomingSchedules.forEach { schedule ->
                    val sdf = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale("vi", "VN"))
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EventNote, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Học bài: ${schedule.deckName}", fontWeight = FontWeight.Bold)
                                Text(text = "Lúc: ${sdf.format(Date(schedule.scheduledTime))}")
                            }
                        }
                    }
                }
                TextButton(onClick = onNavigateToSchedule, modifier = Modifier.align(Alignment.End)) {
                    Text("Quản lý tất cả lịch học >")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Nút vào kho thẻ
            Button(
                onClick = onNavigateToDecks,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.Layers, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Quản lý bộ thẻ của tôi")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StatCard(title: String, count: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(text = count, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = title, style = MaterialTheme.typography.bodySmall)
        }
    }
}
