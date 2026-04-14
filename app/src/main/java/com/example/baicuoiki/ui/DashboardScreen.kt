package com.example.baicuoiki.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baicuoiki.ui.theme.*
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

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

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
                title = { 
                    Text("Flashcard Pro", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary) 
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.AccountCircle, "Profile", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, "Logout", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            ) 
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Text("Chào mừng trở lại! 👋", color = Color.White, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Hôm nay bạn muốn học gì?",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatCard("Bộ thẻ", decks.size.toString(), Icons.Default.Folder, SoftBlue, Modifier.weight(1f))
                    StatCard("Cần ôn", reviewCards.size.toString(), Icons.Default.HistoryEdu, SoftOrange, Modifier.weight(1f))
                }
            }

            item {
                Text("Hành động nhanh", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            item {
                QuickActionCard(
                    title = "Lập lịch học mới",
                    subtitle = "Sắp xếp thời gian học hiệu quả",
                    icon = Icons.Default.EditCalendar,
                    color = SoftGreen,
                    onClick = onNavigateToSchedule
                )
            }

            item {
                Text("Lịch học sắp tới", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            if (upcomingSchedules.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Text(
                            "Chưa có lịch hẹn nào. Hãy lập lịch ngay!",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(upcomingSchedules) { schedule ->
                    val sdf = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale("vi", "VN"))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Alarm, null, tint = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(schedule.deckName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(sdf.format(Date(schedule.scheduledTime)), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = onNavigateToDecks,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(Icons.Default.School, null)
                    Spacer(Modifier.width(8.dp))
                    Text("BẮT ĐẦU HỌC NGAY", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun StatCard(title: String, count: String, icon: ImageVector, bgColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor.copy(alpha = 0.2f))
    ) {
        Column(
            Modifier
                .padding(20.dp)
                .fillMaxWidth(), 
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = bgColor, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(count, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
            Text(title, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

@Composable
fun QuickActionCard(title: String, subtitle: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(color.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.width(20.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
        }
    }
}
