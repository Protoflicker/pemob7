package com.example.myfirstkmpapp.data

data class Note(val id: Int, val title: String, val content: String)

val dummyNotes = listOf(
    Note(1, "Belajar Compose", "Mempelajari navigasi multi-screen"),
    Note(2, "Tugas PAM Minggu 5", "Implementasi Bottom Nav & Drawer"),
    Note(3, "Ide Proyek", "Aplikasi manajemen tugas KMP")
)