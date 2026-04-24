package com.example.myfirstkmpapp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myfirstkmpapp.datastore.SettingsManager
import com.example.myfirstkmpapp.db.DatabaseDriverFactory
import com.example.myfirstkmpapp.db.NotesDatabase
import com.example.myfirstkmpapp.navigation.Screen
import com.example.myfirstkmpapp.network.HttpClientFactory
import com.example.myfirstkmpapp.network.NewsRepository
import com.example.myfirstkmpapp.repository.NoteRepository
import com.example.myfirstkmpapp.screens.*
import com.example.myfirstkmpapp.viewmodel.NewsUiState
import com.example.myfirstkmpapp.viewmodel.NewsViewModel
import com.example.myfirstkmpapp.viewmodel.NotesViewModel
import com.example.myfirstkmpapp.viewmodel.SettingsViewModel
import com.russhwolf.settings.Settings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(driverFactory: DatabaseDriverFactory) {
    val settings = remember { Settings() }
    val settingsManager = remember { SettingsManager(settings) }
    val settingsViewModel = remember { SettingsViewModel(settingsManager) }

    val currentTheme by settingsViewModel.currentTheme.collectAsState()

    val isDark = when (currentTheme) {
        "Dark" -> true
        "Light" -> false
        else -> isSystemInDarkTheme()
    }

    MaterialTheme(colorScheme = if (isDark) darkColorScheme() else lightColorScheme()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val client = remember { HttpClientFactory.create() }
            val newsRepository = remember { NewsRepository(client) }
            val newsViewModel = remember { NewsViewModel(newsRepository) }

            val database = remember { NotesDatabase(driverFactory.createDriver()) }
            val noteRepository = remember { NoteRepository(database) }
            val notesViewModel = remember { NotesViewModel(noteRepository) }

            val navController = rememberNavController()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val isMainScreen = currentRoute in listOf(Screen.Notes.route, Screen.Favorites.route, Screen.Profile.route, "settings")
            val isNewsDetailScreen = currentRoute?.startsWith("news_detail") == true

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        Text("Menu Utama", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                        HorizontalDivider()
                        NavigationDrawerItem(
                            label = { Text("Berita Terkini") },
                            selected = currentRoute == Screen.Notes.route,
                            onClick = {
                                navController.navigate(Screen.Notes.route) { popUpTo(0) }
                                scope.launch { drawerState.close() }
                            },
                            icon = { Icon(Icons.Default.List, null) },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                        NavigationDrawerItem(
                            label = { Text("Buku Catatan") },
                            selected = currentRoute == Screen.Favorites.route,
                            onClick = {
                                navController.navigate(Screen.Favorites.route) { popUpTo(0) }
                                scope.launch { drawerState.close() }
                            },
                            icon = { Icon(Icons.Default.Edit, null) },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                        NavigationDrawerItem(
                            label = { Text("Pengaturan Tema") },
                            selected = currentRoute == "settings",
                            onClick = {
                                navController.navigate("settings") { popUpTo(0) }
                                scope.launch { drawerState.close() }
                            },
                            icon = { Icon(Icons.Default.Settings, null) },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            ) {
                Scaffold(
                    topBar = {
                        if (isMainScreen) {
                            val title = when (currentRoute) {
                                Screen.Favorites.route -> "Catatan Saya"
                                Screen.Profile.route -> "Profil"
                                "settings" -> "Pengaturan Aplikasi"
                                else -> "Berita Tech"
                            }
                            TopAppBar(
                                title = { Text(title) },
                                navigationIcon = {
                                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                        Icon(Icons.Default.Menu, "Menu")
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            )
                        } else if (isNewsDetailScreen) {
                            TopAppBar(
                                title = { Text("Baca Berita", fontSize = 18.sp) },
                                navigationIcon = {
                                    IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            )
                        }
                    },
                    bottomBar = {
                        if (isMainScreen) {
                            NavigationBar {
                                listOf(Screen.Notes, Screen.Favorites, Screen.Profile).forEach { screen ->
                                    NavigationBarItem(
                                        selected = currentRoute == screen.route,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = { Icon(screen.icon!!, null) },
                                        label = {
                                            Text(
                                                when (screen) {
                                                    Screen.Notes -> "Berita"
                                                    Screen.Favorites -> "Catatan"
                                                    else -> screen.label!!
                                                }
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Notes.route,
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable(Screen.Notes.route) {
                            NewsScreen(
                                viewModel = newsViewModel,
                                onArticleClick = { index -> navController.navigate("news_detail/$index") }
                            )
                        }

                        composable(Screen.Favorites.route) {
                            NoteListScreen(
                                viewModel = notesViewModel,
                                onNavigateToAdd = { navController.navigate("add_edit_note/-1") },
                                onNavigateToEdit = { noteId -> navController.navigate("view_note/$noteId") }
                            )
                        }

                        composable(Screen.Profile.route) { ProfileScreen() }
                        composable("settings") {
                            SettingsScreen(viewModel = settingsViewModel)
                        }

                        composable(
                            route = "news_detail/{articleIndex}",
                            arguments = listOf(navArgument("articleIndex") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val index = backStackEntry.arguments?.getInt("articleIndex") ?: 0
                            val state by newsViewModel.uiState.collectAsState()

                            if (state is NewsUiState.Success) {
                                val article = (state as NewsUiState.Success).articles.getOrNull(index)
                                if (article != null) {
                                    NewsDetailScreen(article = article)
                                }
                            }
                        }

                        composable(
                            route = "view_note/{noteId}",
                            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L

                            LaunchedEffect(noteId) {
                                if (noteId != -1L) notesViewModel.selectNote(noteId)
                            }

                            NoteDetailScreen(
                                viewModel = notesViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToEdit = { navController.navigate("add_edit_note/$noteId") }
                            )
                        }

                        composable(
                            route = "add_edit_note/{noteId}",
                            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L

                            LaunchedEffect(noteId) {
                                if (noteId != -1L) {
                                    notesViewModel.selectNote(noteId)
                                } else {
                                    notesViewModel.clearSelectedNote()
                                }
                            }

                            AddEditNoteScreen(
                                viewModel = notesViewModel,
                                onNavigateBack = {
                                    navController.popBackStack(Screen.Favorites.route, inclusive = false)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}