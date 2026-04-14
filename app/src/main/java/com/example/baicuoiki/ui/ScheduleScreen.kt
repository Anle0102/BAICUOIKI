package com.example.baicuoiki.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baicuoiki.data.Deck
import com.example.baicuoiki.data.StudySchedule
import com.example.baicuoiki.worker.WorkManagerHelper
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: FlashcardViewModel,
    onBack: () -> Unit
) {
    val schedules by viewModel.schedules.collectAsState()
    val decks by viewModel.decks.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch học chi tiết") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Đặt lịch mới") }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text(text = "Các buổi học đã hẹn", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            if (schedules.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Chưa có lịch học nào được đặt.")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(schedules) { schedule ->
                        ScheduleItem(schedule = schedule, onDelete = { viewModel.deleteSchedule(it) })
                    }
                }
            }
        }

        if (showAddDialog) {
            AddScheduleDialog(
                decks = decks,
                onDismiss = { showAddDialog = false },
                onConfirm = { deckId, deckName, timeMillis ->
                    // 1. Lưu vào Database
                    viewModel.addSchedule(deckId, deckName, timeMillis)
                    
                    // 2. Hẹn giờ thông báo thông qua WorkManager
                    // Sử dụng thời gian hiện tại làm ID tạm thời để thông báo không bị trùng
                    WorkManagerHelper.scheduleSpecificStudySession(context, System.currentTimeMillis(), deckName, timeMillis)
                    
                    val sdf = SimpleDateFormat("HH:mm - dd/MM", Locale("vi", "VN"))
                    Toast.makeText(context, "Đã đặt lịch: $deckName lúc ${sdf.format(Date(timeMillis))}", Toast.LENGTH_LONG).show()
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun ScheduleItem(schedule: StudySchedule, onDelete: (StudySchedule) -> Unit) {
    val sdf = SimpleDateFormat("EEEE, dd/MM/yyyy - HH:mm", Locale("vi", "VN"))
    val dateString = sdf.format(Date(schedule.scheduledTime))

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Book, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(text = schedule.deckName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.secondary)
                    Spacer(Modifier.width(8.dp))
                    Text(text = dateString, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                }
            }
            IconButton(onClick = { onDelete(schedule) }) {
                Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleDialog(
    decks: List<Deck>,
    onDismiss: () -> Unit,
    onConfirm: (Long, String, Long) -> Unit
) {
    var selectedDeck by remember { mutableStateOf<Deck?>(null) }
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    
    var selectedDateMillis by remember { mutableLongStateOf(0L) }
    var dateText by remember { mutableStateOf("Chọn ngày/tháng/năm") }
    var timeText by remember { mutableStateOf("Chọn giờ học") }
    var selectedHour by remember { mutableIntStateOf(-1) }
    var selectedMinute by remember { mutableIntStateOf(-1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Lên lịch học bài") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column {
                    Text("1. Bạn muốn học gì?", fontWeight = FontWeight.Bold)
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedDeck?.name ?: "Nhấn để chọn bộ thẻ",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            decks.forEach { deck ->
                                DropdownMenuItem(
                                    text = { Text(deck.name) },
                                    onClick = {
                                        selectedDeck = deck
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Column {
                    Text("2. Vào ngày nào?", fontWeight = FontWeight.Bold)
                    OutlinedButton(
                        onClick = {
                            DatePickerDialog(
                                context,
                                { _, y, m, d ->
                                    calendar.set(y, m, d)
                                    selectedDateMillis = calendar.timeInMillis
                                    dateText = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y)
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(dateText)
                    }
                }

                Column {
                    Text("3. Vào lúc mấy giờ?", fontWeight = FontWeight.Bold)
                    OutlinedButton(
                        onClick = {
                            TimePickerDialog(
                                context,
                                { _, h, m ->
                                    selectedHour = h
                                    selectedMinute = m
                                    timeText = String.format(Locale.getDefault(), "%02d:%02d", h, m)
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(timeText)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedDeck != null && selectedDateMillis != 0L && selectedHour != -1) {
                        val finalCalendar = Calendar.getInstance()
                        finalCalendar.timeInMillis = selectedDateMillis
                        finalCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                        finalCalendar.set(Calendar.MINUTE, selectedMinute)
                        finalCalendar.set(Calendar.SECOND, 0)
                        
                        // Kiểm tra nếu thời gian đặt lịch nhỏ hơn hiện tại
                        if (finalCalendar.timeInMillis < System.currentTimeMillis()) {
                            Toast.makeText(context, "Vui lòng chọn thời gian trong tương lai", Toast.LENGTH_SHORT).show()
                        } else {
                            onConfirm(selectedDeck!!.id, selectedDeck!!.name, finalCalendar.timeInMillis)
                        }
                    }
                },
                enabled = selectedDeck != null && selectedDateMillis != 0L && selectedHour != -1
            ) {
                Text("Đặt lịch ngay")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}
