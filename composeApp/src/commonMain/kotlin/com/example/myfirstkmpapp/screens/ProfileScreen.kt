package com.example.myfirstkmpapp.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myfirstkmpapp.components.*
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap

@Composable
fun ProfileScreen() {
    var isEditing by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }
    var profileImage by remember { mutableStateOf<ImageBitmap?>(null) }
    var name by remember { mutableStateOf("Adi Septriansyah") }
    var nim by remember { mutableStateOf("123140021") }
    var bio by remember { mutableStateOf("Mahasiswa Teknik Informatika yang antusias dalam pengembangan perangkat lunak.") }
    var email by remember { mutableStateOf("adi.septriansyah@example.com") }
    var phone by remember { mutableStateOf("+62 812 3456 7890") }
    var location by remember { mutableStateOf("Bandar Lampung, Indonesia") }
    var projects by remember { mutableStateOf("• Aplikasi News Feed (Kotlin)\n• Analisis Spasial QGIS & PostGIS") }

    val scope = rememberCoroutineScope()
    val imagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays -> byteArrays.firstOrNull()?.let { profileImage = it.toImageBitmap() } }
    )

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        ProfileHeader(name, nim, isEditing, profileImage, { imagePicker.launch() }, { name = it }, { nim = it })
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { isEditing = !isEditing }, modifier = Modifier.fillMaxWidth()) {
            Icon(if (isEditing) Icons.Default.Check else Icons.Default.Edit, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isEditing) "Simpan" else "Edit")
        }
        Spacer(modifier = Modifier.height(16.dp))
        ProfileSectionCard("Tentang Saya") {
            if (isEditing) OutlinedTextField(bio, { bio = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Bio") }, minLines = 3)
            else Text(bio, textAlign = TextAlign.Justify, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(16.dp))
        ProfileSectionCard("Kontak") {
            if (isEditing) {
                OutlinedTextField(email, { email = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Email") })
                OutlinedTextField(phone, { phone = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Telepon") })
                OutlinedTextField(location, { location = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Lokasi") })
            } else {
                ContactInfoItem(Icons.Default.Email, email)
                ContactInfoItem(Icons.Default.Phone, phone)
                ContactInfoItem(Icons.Default.LocationOn, location)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        if (!isEditing) {
            OutlinedButton(onClick = { showDetails = !showDetails }, modifier = Modifier.fillMaxWidth()) { Text(if (showDetails) "Sembunyikan" else "Tampilkan Proyek") }
        }
        AnimatedVisibility(visible = showDetails || isEditing) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                ProfileSectionCard("Proyek") {
                    if (isEditing) OutlinedTextField(projects, { projects = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Proyek") }, minLines = 3)
                    else Text(projects, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}