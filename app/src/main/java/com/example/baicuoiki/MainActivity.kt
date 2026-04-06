package com.example.baicuoiki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.baicuoiki.ui.*
import com.example.baicuoiki.ui.theme.BAICUOIKITheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        setContent {
            BAICUOIKITheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = hiltViewModel()
                val flashcardViewModel: FlashcardViewModel = hiltViewModel()
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login") {
                            LoginScreen(
                                viewModel = authViewModel,
                                onLoginSuccess = {
                                    navController.navigate("dashboard") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    authViewModel.resetState()
                                    navController.navigate("register")
                                }
                            )
                        }
                        composable("register") {
                            RegisterScreen(
                                viewModel = authViewModel,
                                onRegisterSuccess = {
                                    navController.navigate("login") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                },
                                onNavigateToLogin = {
                                    authViewModel.resetState()
                                    navController.navigate("login")
                                }
                            )
                        }
                        composable("dashboard") {
                            DashboardScreen(
                                viewModel = flashcardViewModel,
                                onNavigateToDecks = {
                                    navController.navigate("decks")
                                },
                                onNavigateToSchedule = {
                                    navController.navigate("schedule")
                                },
                                onNavigateToProfile = {
                                    navController.navigate("profile")
                                },
                                onLogout = {
                                    authViewModel.logout()
                                    navController.navigate("login") {
                                        popUpTo("dashboard") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("profile") {
                            ProfileScreen(
                                authViewModel = authViewModel,
                                onBack = {
                                    navController.popBackStack()
                                },
                                onLogout = {
                                    navController.navigate("login") {
                                        popUpTo("dashboard") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("schedule") {
                            ScheduleScreen(
                                viewModel = flashcardViewModel,
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("decks") {
                            DeckScreen(
                                viewModel = flashcardViewModel,
                                onDeckClick = { deckId ->
                                    navController.navigate("study/$deckId")
                                },
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("study/{deckId}") { backStackEntry ->
                            val deckId = backStackEntry.arguments?.getString("deckId")?.toLongOrNull() ?: 0L
                            StudyScreen(
                                deckId = deckId,
                                viewModel = flashcardViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
