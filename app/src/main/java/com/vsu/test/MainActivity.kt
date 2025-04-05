package com.vsu.test

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vsu.test.data.storage.TokenManager
import com.vsu.test.presentation.ui.screens.AboutScreen
import com.vsu.test.presentation.ui.screens.LoginScreen
import com.vsu.test.presentation.ui.screens.MapScreen
import com.vsu.test.presentation.ui.screens.MoreScreen
import com.vsu.test.presentation.ui.screens.RegistrationScreen
import com.vsu.test.presentation.ui.screens.SettingsScreen
import com.vsu.test.presentation.ui.theme.TestTheme
import com.vsu.test.service.LocationService
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.app.ActivityManager
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.vsu.test.presentation.ui.screens.EditProfileScreen
import com.vsu.test.presentation.ui.screens.ProfileScreen
import com.vsu.test.presentation.ui.screens.ReviewScreen

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
                    Screen.Map.route()
                } else {
                    Screen.Login.route()
                }
                LaunchedEffect(authState) {
                    if (!authState) {
                        navController.navigate(Screen.Login.route()) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                }
                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    composable(Screen.Login.route()) {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate(Screen.Map.route()) {
                                    popUpTo(Screen.Login.route()) { inclusive = true }
                                }
                            },
                            onNavigateToRegistration = {
                                navController.navigate(Screen.Registration.route())
                            }
                        )
                    }
                    composable(Screen.Registration.route()) {
                        RegistrationScreen(
                            onNavigateToLogin = { navController.navigate(Screen.Login.route()) }
                        )
                    }
                    composable(Screen.Map.route()) {
                        MapScreen(
                            onNavigateToMore = { navController.navigate(Screen.More.route()) },
                            navController = navController)
                    }
                    composable(Screen.More.route()) {
                        MoreScreen(
                            onNavigateToSettings = { navController.navigate(Screen.Settings.route()) },
                            onNavigateToMap = { navController.navigate(Screen.Map.route()) },
                            onNavigateToProfile = {
                                val currentUserId = tokenManager.getId()
                                navController.navigate(Screen.Profile.route(currentUserId))
                            },
                            navController = navController
                        )
                    }
                    composable(Screen.Settings.route()) {
                        SettingsScreen(
                            onNavigateToAbout = { navController.navigate(Screen.About.route()) },
                            onNavigateToMore = { navController.navigate(Screen.More.route()) },
                            navController = navController,
                            tokenManager = tokenManager
                        )
                    }
                    composable(Screen.About.route()) {
                        AboutScreen(
                            onNavigateToSettings = { navController.navigate(Screen.Settings.route()) }
                        )
                    }
                    composable(
                        route = Screen.Profile.routePattern,
                        arguments = listOf(navArgument("profileId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val profileId = backStackEntry.arguments?.getString("profileId")?.toLong() ?: return@composable
                        val currentProfileId = tokenManager.getId()
                        val isOwnProfile = profileId == currentProfileId
                        ProfileScreen(
                            profileId = profileId,
                            isOwnProfile = isOwnProfile,
                            onEditProfile = { navController.navigate(Screen.EditProfile.route()) },
                            onBackButton = { navController.popBackStack() },
                            navController = navController
                        )
                    }
                    composable(route = Screen.EditProfile.route()) {
                        EditProfileScreen(
                            onBackClick = { navController.popBackStack() },
                            onProfileUpdated = { navController.popBackStack() }
                        )
                    }
                    composable(
                        route = Screen.Reviews.routePattern,
                        arguments = listOf(navArgument("profileId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val profileId = backStackEntry.arguments?.getString("profileId")?.toLong() ?: return@composable

                        ReviewScreen(
                            profileId = profileId,
                            onBackClick = { navController.popBackStack() },
                        )
                    }
                }
                handleIntent(intent, navController)
            }
        }
        checkTrackingState()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        MapKitFactory.getInstance().onStop()
    }

    private fun handleIntent(intent: Intent?, navController: NavHostController) {
        intent?.let {
            if (it.getStringExtra("screen") == "more") {
                navController.navigate(Screen.More.route())
            }
        }
    }

    private fun checkTrackingState() {
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val isTrackingEnabled = prefs.getBoolean("tracking_enabled", false)
        val intent = Intent(this, LocationService::class.java)
        if (isTrackingEnabled && !isServiceRunning(LocationService::class.java)) {
            startForegroundService(intent)
            Log.d("MainActivity", "Service started on app launch")
        }
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Integer.MAX_VALUE)
            .any { it.service.className == serviceClass.name }
    }
}

sealed class Screen {
    abstract val routePattern: String
    open fun route(vararg args: Any): String = routePattern

    object Registration : Screen() {
        override val routePattern = "registration"
    }

    object Map : Screen() {
        override val routePattern = "map"
    }

    object Login : Screen() {
        override val routePattern = "login"
    }

    object More : Screen() {
        override val routePattern = "more"
    }

    object Settings : Screen() {
        override val routePattern = "settings"
    }

    object About : Screen() {
        override val routePattern = "about"
    }

    object Profile : Screen() {
        override val routePattern = "profile/{profileId}"
        override fun route(vararg args: Any): String {
            require(args.size == 1 && args[0] is String) { "Profile requires userId: String" }
            return "profile/${args[0]}"
        }
    }
    object Reviews : Screen() {
        override val routePattern = "reviews/{profileId}"
        override fun route(vararg args: Any): String {
            require(args.size == 1 && args[0] is String) { "Profile requires userId: String" }
            return "reviews/${args[0]}"
        }
    }

    object EditProfile : Screen() {
        override val routePattern = "edit_profile"
    }
}