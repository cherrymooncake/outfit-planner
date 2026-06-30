package com.cherrymooncake.wardrobe_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cherrymooncake.wardrobe_android.core.navigation.NavRoutes
import com.cherrymooncake.wardrobe_android.core.network.source.ITokenLocalSource
import com.cherrymooncake.wardrobe_android.feature.admin.presentation.ui.AdminScreen
import com.cherrymooncake.wardrobe_android.feature.auth.presentation.ui.LoginScreen
import com.cherrymooncake.wardrobe_android.feature.auth.presentation.ui.RegisterScreen
import com.cherrymooncake.wardrobe_android.feature.ootd.presentation.ui.CalendarScreen
import com.cherrymooncake.wardrobe_android.feature.ootd.presentation.ui.OotdScreen
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.ui.OutfitsScreen
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.ui.constructor.OutfitConstructorScreen
import com.cherrymooncake.wardrobe_android.feature.profile.presentation.ui.ProfileScreen
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.ui.WardrobeScreen
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.ui.mask.MaskEditorScreen
import com.cherrymooncake.wardrobe_android.ui.theme.WardrobeAndroidTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

       @Inject
    lateinit var tokenSource: ITokenLocalSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WardrobeAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(tokenSource)
                }
            }
        }
    }
}

@Composable
fun MainScreen(tokenSource: ITokenLocalSource) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

        val userRole = remember(navBackStackEntry) { tokenSource.getUserRole() }

    val showBottomBar = currentDestination?.route in listOf(
        NavRoutes.Wardrobe.route,
        NavRoutes.Outfits.route,
        NavRoutes.Profile.route,
        NavRoutes.Admin.route,
        NavRoutes.Calendar.route,
        NavRoutes.Ootd.route
    ) || currentDestination?.route?.startsWith("ootd") == true

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    val navItemColors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = androidx.compose.ui.graphics.Color.Gray
                    )


                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Star, "Сегодня") },
                        selected = currentDestination?.route?.startsWith("ootd") == true,
                        colors = navItemColors,
                        onClick = {
                            navController.navigate(NavRoutes.Ootd.createRoute()) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true; restoreState = true
                            }
                        }
                    )


                    NavigationBarItem(
                        icon = { Icon(Icons.Default.DateRange, "Календарь") },
                        selected = currentDestination?.hierarchy?.any { it.route == NavRoutes.Calendar.route } == true,
                        colors = navItemColors,
                        onClick = {
                            navController.navigate(NavRoutes.Calendar.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true; restoreState = true
                            }
                        }
                    )


                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Checkroom, "Шкаф") },
                        selected = currentDestination?.hierarchy?.any { it.route == NavRoutes.Wardrobe.route } == true,
                        colors = navItemColors,
                        onClick = {
                            navController.navigate(NavRoutes.Wardrobe.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true; restoreState = true
                            }
                        }
                    )


                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Style, "Образы") },
                        selected = currentDestination?.hierarchy?.any { it.route == NavRoutes.Outfits.route } == true,
                        colors = navItemColors,
                        onClick = {
                            navController.navigate(NavRoutes.Outfits.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true; restoreState = true
                            }
                        }
                    )


                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, "Профиль") },
                        selected = currentDestination?.hierarchy?.any { it.route == NavRoutes.Profile.route } == true,
                        colors = navItemColors,
                        onClick = {
                            navController.navigate(NavRoutes.Profile.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true; restoreState = true
                            }
                        }
                    )


                    if (userRole == "Admin") {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.AdminPanelSettings, "Админ") },
                            selected = currentDestination?.hierarchy?.any { it.route == NavRoutes.Admin.route } == true,
                            colors = navItemColors,
                            onClick = {
                                navController.navigate(NavRoutes.Admin.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true; restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = NavRoutes.Login.route) {
                LoginScreen(
                    onNavigateToRegister = { navController.navigate(NavRoutes.Register.route) },
                    onNavigateToWardrobe = {
                        navController.navigate(NavRoutes.Ootd.createRoute()) {
                            popUpTo(NavRoutes.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(route = NavRoutes.Register.route) {
                RegisterScreen(onNavigateBack = { navController.popBackStack() })
            }


            composable(
                route = NavRoutes.Ootd.route,
                arguments = listOf(
                    navArgument("date") { type = NavType.StringType; nullable = true; defaultValue = null }
                )
            ) { entry ->
                OotdScreen(
                    targetDate = entry.arguments?.getString("date"),
                    onNavigateToConstructor = { dateStr ->
                        navController.navigate(NavRoutes.OutfitConstructor.createRoute(date = dateStr))
                    }
                )
            }

            composable(route = NavRoutes.Calendar.route) {
                CalendarScreen(
                    onDayClick = { dateStr ->
                        navController.navigate(NavRoutes.Ootd.createRoute(dateStr))
                    }
                )
            }


            composable(route = NavRoutes.Wardrobe.route) {
                WardrobeScreen(onNavigateToMaskEditor = { id -> navController.navigate(NavRoutes.MaskEditor.createRoute(id)) })
            }

            composable(route = NavRoutes.MaskEditor.route) { entry ->
                MaskEditorScreen(
                    itemId = entry.arguments?.getString("itemId") ?: "",
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(route = NavRoutes.Outfits.route) {
                OutfitsScreen(
                    onNavigateToConstructor = { outfitId, templateId, isTemplateEdit ->
                        navController.navigate(NavRoutes.OutfitConstructor.createRoute(outfitId, templateId, isTemplateEdit))
                    }
                )
            }

            composable(
                route = NavRoutes.OutfitConstructor.route,
                arguments = listOf(
                    navArgument("outfitId") { type = NavType.StringType; nullable = true; defaultValue = null },
                    navArgument("templateId") { type = NavType.StringType; nullable = true; defaultValue = null },
                    navArgument("mode") { type = NavType.StringType; nullable = true; defaultValue = null },
                    navArgument("date") { type = NavType.StringType; nullable = true; defaultValue = null }
                )
            ) { entry ->
                val outfitId = entry.arguments?.getString("outfitId")
                val templateId = entry.arguments?.getString("templateId")
                val isTemplateEdit = entry.arguments?.getString("mode") == "edit_template"
                val date = entry.arguments?.getString("date")

                OutfitConstructorScreen(
                    outfitId = outfitId,
                    templateId = templateId,
                    isTemplateEditMode = isTemplateEdit,
                    targetDate = date,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(route = NavRoutes.Profile.route) {
                ProfileScreen(
                    onNavigateToLogin = {
                        navController.navigate(NavRoutes.Login.route) { popUpTo(0) { inclusive = true } }
                    }
                )
            }

            composable(route = NavRoutes.Admin.route) {
                AdminScreen()
            }
        }
    }
}