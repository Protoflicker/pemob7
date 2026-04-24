package com.example.myfirstkmpapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myfirstkmpapp.viewmodel.NotesViewModel

// 3 Import super penting yang sebelumnya hilang:
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    viewModel: NotesViewModel,
    onNavigateBack: () -> Unit
) {
    val selectedNote by viewModel.selectedNote.collectAsState()

    var title by remember(selectedNote) { mutableStateOf(selectedNote?.title ?: "") }
    var content by remember(selectedNote) { mutableStateOf(selectedNote?.content ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (selectedNote == null) "Buat Catatan" else "Edit Catatan") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearSelectedNote()
                        onNavigateBack()
                    }) { Icon(Icons.Default.ArrowBack, "Kembali") }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                if (selectedNote == null) {
                                    viewModel.addNote(title, content)
                                } else {
                                    viewModel.updateNote(selectedNote!!.id, title, content)
                                }
                                viewModel.clearSelectedNote()
                                onNavigateBack()
                            }
                        }
                    ) { Icon(Icons.Default.Check, "Simpan") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Judul Catatan") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Isi Catatan") },
                modifier = Modifier.fillMaxWidth().weight(1f),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
            )
        }
    }
}