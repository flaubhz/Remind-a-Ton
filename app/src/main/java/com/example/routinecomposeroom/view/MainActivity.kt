package com.example.routinecomposeroom.view

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.routinecomposeroom.view.theme.RoutineComposeRoomTheme
import com.example.routinecomposeroom.view.ui.screens.*
import com.example.routinecomposeroom.viewmodel.ThemeMode
import com.example.routinecomposeroom.viewmodel.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {

            val currentTheme = themeViewModel.themeState.value

            val useDarkTheme = when (currentTheme) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            RoutineComposeRoomTheme(darkTheme = useDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {
                            HomeScreen(
                                onNavigateToAllRoutines = { navController.navigate("all_routines") },
                                onNavigateToOptions = { navController.navigate("options") },
                                onRoutineClick = { routineId ->
                                    navController.navigate("routine_detail/$routineId")
                                },
                            )
                        }

                        composable(route = "all_routines") {
                            RoutinesScreen(
                                onNavigateToHome = {
                                    navController.navigate("home") { popUpTo("home") { inclusive = true } }
                                },
                                onNavigateToOptions = { navController.navigate("options") },
                            )
                        }

                        composable("options") {
                            OptionsScreen(
                                onNavigateToHome = {
                                    navController.navigate("home") { popUpTo("home") { inclusive = true } }
                                },
                                onNavigateToAllRoutines = { navController.navigate("all_routines") },
                                onNavigateToConfig = { navController.navigate("config") },
                                onNavigateToPrivacy = { navController.navigate("privacy_policy") }
                            )
                        }

                        composable("privacy_policy") {
                            PolicyScreen(onNavigateBack = { navController.popBackStack() })
                        }

                        composable("config") {
                            ConfigScreen(onNavigateBack = { navController.popBackStack() })
                        }

                        composable(
                            route = "routine_detail/{routineId}",
                            arguments = listOf(navArgument("routineId") { type = NavType.IntType })
                        ) { backStackEntry -> // Agregamos 'backStackEntry' aquí

                            // Extraemos el ID de los argumentos de la navegación
                            val routineId = backStackEntry.arguments?.getInt("routineId") ?: 0

                            RoutineDetailScreen(
                                routineId = routineId, // Pasamos el ID que acabamos de extraer
                                onNavigateBack = { navController.popBackStack() },
                                isReadOnly = true
                            )
                        }
                    }
                }
            }
        }
    }
}


