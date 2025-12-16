package com.example.diariodeviaje.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.diariodeviaje.navigation.AppScreen
import com.example.diariodeviaje.navigation.BottomNavItem
import com.example.diariodeviaje.ui.components.BottomNavBar
import com.example.diariodeviaje.viewmodel.ActividadViewModelFactory
import com.example.diariodeviaje.viewmodel.HomeViewModel
import com.example.diariodeviaje.viewmodel.ProfileViewModel
import com.example.diariodeviaje.viewmodel.RecordViewModel

@Composable
fun MainScreen(
    viewModelFactory: ActividadViewModelFactory,
    mainNavController: NavHostController
) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavBar(navController = bottomNavController)
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues = innerPadding)
        ) {
            composable(route = BottomNavItem.Home.route) {
                val homeViewModel: HomeViewModel = viewModel(factory = viewModelFactory)
                HomeScreen(
                    homeViewModel = homeViewModel,
                    navController = mainNavController,
                    onSoporteClick = {
                        mainNavController.navigate(route = AppScreen.Soporte.route)
                    },
                    onActividadClick = { id ->
                        mainNavController.navigate(AppScreen.Detail.createRoute(id))
                    }
                )
            }

            composable(route = BottomNavItem.Record.route) {
                val recordViewModel: RecordViewModel = viewModel(factory = viewModelFactory)

                RecordScreen(
                    viewModel = recordViewModel,
                    onNavigate = { route ->
                        mainNavController.navigate(route)
                    }
                )
            }

            composable(route = BottomNavItem.Profile.route) {
                val profileViewModel: ProfileViewModel = viewModel(factory = viewModelFactory)

                ProfileScreen(
                    viewModel = profileViewModel,
                    onLogout = {
                        mainNavController.navigate(AppScreen.Login.route) {
                            popUpTo(0)
                        }
                    },
                    onActividadClick = { id ->
                        mainNavController.navigate(AppScreen.Detail.createRoute(id))
                    }
                )
            }
        }
    }
}