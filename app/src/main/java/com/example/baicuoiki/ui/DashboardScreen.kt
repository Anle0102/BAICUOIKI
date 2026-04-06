package com.example.baicuoiki.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    
    // XỬ LÝ QUYỀN THÔNG BÁO (Android 13+)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Xử lý nếu cần
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

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

            Card(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                onClick = onNavigateToSchedule,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.EditCalendar, null, Modifier.size(48.dp), MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Lên lịch học bài mới", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                        Text("Đặt ngày, giờ và chọn bộ thẻ", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard("Bộ thẻ", decks.size.toString(), Icons.Default.Folder, Modifier.weight(1f))
                StatCard("Cần ôn", reviewCards.size.toString(), Icons.Default.HistoryEdu, Modifier.weight(1f))
            }

            Text("Lịch học sắp tới", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            if (upcomingSchedules.isEmpty()) {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Text("Chưa có lịch hẹn học. Nhấn nút xanh để đặt lịch!", Modifier.padding(16.dp))
                }
            } else {
                upcomingSchedules.forEach { schedule ->
                    val sdf = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale("vi", "VN"))
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EventNote, null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Học bài: ${schedule.deckName}", fontWeight = FontWeight.Bold)
                                Text("Lúc: ${sdf.format(Date(schedule.scheduledTime))}")
                            }
                        }
                    }
                }
                TextButton(onClick = onNavigateToSchedule, modifier = Modifier.align(Alignment.End)) {
                    Text("Quản lý tất cả lịch học >")
                }
            }

            Spacer(Modifier.weight(1f))
            Button(onClick = onNavigateToDecks, Modifier.fillMaxWidth().height(56.dp), shape = MaterialTheme.shapes.medium) {
                Icon(Icons.Default.Layers, null)
                Spacer(Modifier.width(8.dp))
                Text("Quản lý bộ thẻ của tôi")
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun StatCard(title: String, count: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Text(count, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.bodySmall)
        }
    }
}
