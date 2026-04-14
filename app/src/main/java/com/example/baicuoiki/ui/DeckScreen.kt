package com.example.baicuoiki.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.baicuoiki.data.Deck

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckScreen(
    viewModel: FlashcardViewModel,
    onDeckClick: (Long) -> Unit,
    onBack: () -> Unit
) {
    val decks by viewModel.decks.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Deck?>(null) }
    
    // Sử dụng TextFieldValue để hỗ trợ gõ Tiếng Việt
    var deckName by remember { mutableStateOf(TextFieldValue("")) }
    var deckDescription by remember { mutableStateOf(TextFieldValue("")) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    val filteredDecks = decks.filter { 
        it.name.contains(searchQuery.text, ignoreCase = true) || 
        it.description.contains(searchQuery.text, ignoreCase = true) 
    }

    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("Bộ thẻ ghi nhớ") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            ) 
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                deckName = TextFieldValue("")
                deckDescription = TextFieldValue("")
                showAddDialog = true 
            }) {
                Icon(Icons.Default.Add, contentDescription = "Thêm bộ thẻ")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Thanh tìm kiếm hỗ trợ Tiếng Việt
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                placeholder = { Text("Tìm kiếm bộ thẻ...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredDecks) { deck ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { onDeckClick(deck.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = deck.name,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                if (deck.description.isNotBlank()) {
                                    Text(
                                        text = deck.description,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            Row {
                                IconButton(onClick = { 
                                    deckName = TextFieldValue(deck.name)
                                    deckDescription = TextFieldValue(deck.description)
                                    showEditDialog = deck 
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Sửa")
                                }
                                IconButton(onClick = { viewModel.deleteDeck(deck) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Xóa")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dialog Thêm
        if (showAddDialog) {
            DeckDialog(
                title = "Thêm bộ thẻ mới",
                name = deckName,
                description = deckDescription,
                onNameChange = { deckName = it },
                onDescriptionChange = { deckDescription = it },
                onConfirm = {
                    if (deckName.text.isNotBlank()) {
                        viewModel.addDeck(deckName.text, deckDescription.text)
                        showAddDialog = false
                    }
                },
                onDismiss = { showAddDialog = false }
            )
        }

        // Dialog Sửa
        showEditDialog?.let { deck ->
            DeckDialog(
                title = "Sửa bộ thẻ",
                name = deckName,
                description = deckDescription,
                onNameChange = { deckName = it },
                onDescriptionChange = { deckDescription = it },
                onConfirm = {
                    if (deckName.text.isNotBlank()) {
                        viewModel.updateDeck(deck.copy(name = deckName.text, description = deckDescription.text))
                        showEditDialog = null
                    }
                },
                onDismiss = { showEditDialog = null }
            )
        }
    }
}

@Composable
fun DeckDialog(
    title: String,
    name: TextFieldValue,
    description: TextFieldValue,
    onNameChange: (TextFieldValue) -> Unit,
    onDescriptionChange: (TextFieldValue) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Tên bộ thẻ") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Mô tả") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
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
