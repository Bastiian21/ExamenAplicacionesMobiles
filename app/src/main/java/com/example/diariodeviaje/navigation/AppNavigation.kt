package com.example.diariodeviaje.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.diariodeviaje.ViajesApplication
import com.example.diariodeviaje.ui.screens.DetailScreen
import com.example.diariodeviaje.ui.screens.LoginScreen
import com.example.diariodeviaje.ui.screens.MainScreen
import com.example.diariodeviaje.ui.screens.RegisterScreen
import com.example.diariodeviaje.ui.screens.SaveScreen
import com.example.diariodeviaje.ui.screens.SoporteScreen
import com.example.diariodeviaje.ui.screens.SplashScreen
import com.example.diariodeviaje.ui.screens.UserProfileScreen
import com.example.diariodeviaje.viewmodel.ActividadViewModelFactory
import com.example.diariodeviaje.viewmodel.DetailViewModel
import com.example.diariodeviaje.viewmodel.LoginViewModel
import com.example.diariodeviaje.viewmodel.SaveViewModel

object NavArgs {
    const val DISTANCIA = "distancia"
    const val TIEMPO = "tiempo"
    const val LAT_INICIO = "lat_inicio"
    const val LON_INICIO = "lon_inicio"
    const val LAT_FIN = "lat_fin"
    const val LON_FIN = "lon_fin"
}

sealed class AppScreen(val route: String) {
    object Splash : AppScreen("splash_screen")
    object Login : AppScreen("login_screen")
    object Main : AppScreen("main_screen")
    object Soporte : AppScreen("soporte_screen")
    object Register : AppScreen("register_screen")

    object Save : AppScreen("save_screen/{${NavArgs.DISTANCIA}}/{${NavArgs.TIEMPO}}/{${NavArgs.LAT_INICIO}}/{${NavArgs.LON_INICIO}}/{${NavArgs.LAT_FIN}}/{${NavArgs.LON_FIN}}") {
        fun createRoute(distancia: Double, tiempo: Long, latInicio: Double, lonInicio: Double, latFin: Double, lonFin: Double) =
            "save_screen/$distancia/$tiempo/$latInicio/$lonInicio/$latFin/$lonFin"
    }

    object Detail : AppScreen("detail_screen/{actividadId}") {
        fun createRoute(id: Long) = "detail_screen/$id"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val application = context.applicationContext as ViajesApplication
    val repository = application.repository
    val userPreferences = application.userPreferences
    val locationService = application.locationService

    val viewModelFactory = ActividadViewModelFactory(
        repository, userPreferences, application, locationService
    )

    NavHost(
        navController = navController,
        startDestination = AppScreen.Splash.route
    ) {
        composable(AppScreen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(AppScreen.Login.route) {
            val loginViewModel: LoginViewModel = viewModel(factory = viewModelFactory)
            LoginScreen(navController = navController, viewModel = loginViewModel)
        }
        composable(AppScreen.Main.route) {
            MainScreen(viewModelFactory = viewModelFactory, mainNavController = navController)
        }
        composable(AppScreen.Soporte.route) {
            SoporteScreen(navController = navController)
        }
        composable(AppScreen.Register.route) {
            RegisterScreen(navController)
        }
        composable(
            route = "user_profile/{nombre}/{email}",
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            UserProfileScreen(navController, nombre, email)
        }

        composable(
            route = AppScreen.Save.route,
            arguments = listOf(
                navArgument(NavArgs.DISTANCIA) { type = NavType.FloatType },
                navArgument(NavArgs.TIEMPO) { type = NavType.LongType },
                navArgument(NavArgs.LAT_INICIO) { type = NavType.FloatType },
                navArgument(NavArgs.LON_INICIO) { type = NavType.FloatType },
                navArgument(NavArgs.LAT_FIN) { type = NavType.FloatType },
                navArgument(NavArgs.LON_FIN) { type = NavType.FloatType }
            )
        ) {
            val saveViewModel: SaveViewModel = viewModel(factory = viewModelFactory)
            SaveScreen(navController = navController, viewModel = saveViewModel)
        }

        composable(
            route = AppScreen.Detail.route,
            arguments = listOf(navArgument("actividadId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("actividadId") ?: 0L
            val detailViewModel = DetailViewModel(repository, id)
            DetailScreen(navController = navController, viewModel = detailViewModel)
        }
    }
}