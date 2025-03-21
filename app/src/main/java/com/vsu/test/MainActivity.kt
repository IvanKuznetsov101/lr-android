package com.vsu.test


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.vsu.test.data.TokenManager
import com.vsu.test.presentation.ui.screens.AboutScreen
import com.vsu.test.presentation.ui.screens.LoginScreen
import com.vsu.test.presentation.ui.screens.MapScreen
import com.vsu.test.presentation.ui.screens.MoreScreen
import com.vsu.test.presentation.ui.screens.RegistrationScreen
import com.vsu.test.presentation.ui.screens.SettingsScreen
import com.vsu.test.presentation.ui.theme.TestTheme
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TestTheme {
                val navController = rememberNavController()
                val authState by tokenManager.authState.collectAsState()
                val startDestination = if (tokenManager.isLoggedIn()) {
                    Screen.Map.route
                } else {
                    Screen.Login.route
                }
                LaunchedEffect(authState) {
                    if (!authState) {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                }
                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    composable(Screen.Login.route) {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate(Screen.Map.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            },
                            onNavigateToRegistration = {
                                navController.navigate(Screen.Registration.route)
                            }
                        )
                    }
                    composable(Screen.Registration.route) {
                        RegistrationScreen(
                            onNavigateToLogin = { navController.navigate(Screen.Login.route) }
                        )
                    }
                    composable(Screen.Map.route) {
                        MapScreen(onNavigateToMore = { navController.navigate(Screen.More.route) })
                    }
                    composable(Screen.More.route) {
                        MoreScreen(
                            onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                            onNavigateToMap =  { navController.navigate(Screen.Map.route) })
                    }
                    composable(Screen.Settings.route) {
                        SettingsScreen(
                            onNavigateToAbout = { navController.navigate(Screen.About.route) },
                            onNavigateToMore = {navController.navigate(Screen.More.route)}
                            )
                    }
                    composable(Screen.About.route) {
                        AboutScreen(
                            onNavigateToSettings = {navController.navigate(Screen.Settings.route)}
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        MapKitFactory.getInstance().onStop()
    }
}

sealed class Screen(val route: String) {
    object Registration : Screen("registration")
    object Map : Screen("map")
    object Login : Screen("login")
    object More : Screen("more")
    object Settings : Screen("settings")
    object About : Screen("about")

}