package com.example.myfirstkmpapp

import androidx.compose.ui.window.ComposeUIViewController
import com.example.myfirstkmpapp.db.DatabaseDriverFactory

fun MainViewController() = ComposeUIViewController {
    App(driverFactory = DatabaseDriverFactory())
}