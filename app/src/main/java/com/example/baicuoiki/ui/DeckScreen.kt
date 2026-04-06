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
    
    var deckName by remember { mutableStateOf("") }
    var deckDescription by remember { mutableStateOf("") }
    
    var searchQuery by remember { mutableStateOf("") }
    val filteredDecks = decks.filter { 
        it.name.contains(searchQuery, ignoreCase = true) || 
        it.description.contains(searchQuery, ignoreCase = true) 
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
                deckName = ""
                deckDescription = ""
                showAddDialog = true 
            }) {
                Icon(Icons.Default.Add, contentDescription = "Thêm bộ thẻ")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Thanh tìm kiếm
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
                                    deckName = deck.name
                                    deckDescription = deck.description
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
                    if (deckName.isNotBlank()) {
                        viewModel.addDeck(deckName, deckDescription)
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
                    if (deckName.isNotBlank()) {
                        viewModel.updateDeck(deck.copy(name = deckName, description = deckDescription))
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
    name: String,
    description: String,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Tên bộ thẻ") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Mô tả") },
                    modifier = Modifier.fillMaxWidth()
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
