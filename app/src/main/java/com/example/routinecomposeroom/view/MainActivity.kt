package com.example.routinecomposeroom.view

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.routinecomposeroom.view.theme.RoutineComposeRoomTheme
import com.example.routinecomposeroom.view.ui.screens.HomeScreen
import com.example.routinecomposeroom.view.ui.screens.RoutineDetailScreen
import com.example.routinecomposeroom.view.ui.screens.RoutinesScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            RoutineComposeRoomTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Gestionar apertura desde notificación
                    val routineIdFromNotification = intent.getIntExtra("ROUTINE_ID_EXTRA", -1)

                    LaunchedEffect(routineIdFromNotification) {
                        if (routineIdFromNotification != -1) {
                            navController.navigate("routine_detail/$routineIdFromNotification")
                        }
                    }


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
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                onNavigateToOptions = { navController.navigate("options") },
                            )
                        }


                        composable("options") {
                            com.example.routinecomposeroom.view.ui.screens.OptionsScreen(
                                onNavigateToHome = {
                                    navController.navigate("home") { popUpTo("home") { inclusive = true } }
                                },
                                onNavigateToAllRoutines = { navController.navigate("all_routines") },
                                onNavigateToPrivacy = { navController.navigate("privacy_policy") }
                            )
                        }

                        composable("privacy_policy") {

                            com.example.routinecomposeroom.view.ui.screens.PolicyScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(
                            route = "routine_detail/{routineId}",
                            arguments = listOf(navArgument("routineId") { type = NavType.IntType })
                        ) {
                            RoutineDetailScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

