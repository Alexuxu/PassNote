package com.ax.passnote.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ax.passnote.PassNoteApplication
import com.ax.passnote.ui.add.AddPasswordScreen
import com.ax.passnote.ui.detail.PasswordDetailScreen
import com.ax.passnote.ui.edit.EditPasswordScreen
import com.ax.passnote.ui.main.MainScreen
import com.ax.passnote.ui.more.MoreScreen
import com.ax.passnote.ui.settings.SettingsScreen

object AppDestinations {
    const val MAIN_ROUTE = "main"
    const val ADD_PASSWORD_ROUTE = "add_password"
    const val DETAIL_ROUTE = "detail"
    const val EDIT_ROUTE = "edit"
    const val MORE_ROUTE = "more"
    const val SETTINGS_ROUTE = "settings"
    const val DETAIL_ID_KEY = "passwordId"
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val application = LocalContext.current.applicationContext as PassNoteApplication

    NavHost(navController = navController, startDestination = AppDestinations.MAIN_ROUTE) {
        composable(AppDestinations.MAIN_ROUTE) {
            MainScreen(
                application = application,
                onAddPassword = { navController.navigate(AppDestinations.ADD_PASSWORD_ROUTE) },
                onPasswordClick = { passwordId ->
                    navController.navigate("${AppDestinations.DETAIL_ROUTE}/$passwordId")
                },
                onMoreClick = { navController.navigate(AppDestinations.MORE_ROUTE) }
            )
        }
        composable(AppDestinations.ADD_PASSWORD_ROUTE) {
            AddPasswordScreen(
                application = application,
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }
        composable(
            route = "${AppDestinations.DETAIL_ROUTE}/{${AppDestinations.DETAIL_ID_KEY}}",
            arguments = listOf(navArgument(AppDestinations.DETAIL_ID_KEY) { type = NavType.IntType })
        ) { backStackEntry ->
            val passwordId = backStackEntry.arguments?.getInt(AppDestinations.DETAIL_ID_KEY) ?: -1
            PasswordDetailScreen(
                application = application,
                passwordId = passwordId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate("${AppDestinations.EDIT_ROUTE}/$passwordId") }
            )
        }
        composable(
            route = "${AppDestinations.EDIT_ROUTE}/{${AppDestinations.DETAIL_ID_KEY}}",
            arguments = listOf(navArgument(AppDestinations.DETAIL_ID_KEY) { type = NavType.IntType })
        ) { backStackEntry ->
            val passwordId = backStackEntry.arguments?.getInt(AppDestinations.DETAIL_ID_KEY) ?: -1
            EditPasswordScreen(
                application = application,
                passwordId = passwordId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(AppDestinations.MORE_ROUTE) { 
            MoreScreen(
                application = application,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate(AppDestinations.SETTINGS_ROUTE) }
            )
        }
        composable(AppDestinations.SETTINGS_ROUTE) {
            SettingsScreen(
                application = application,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
