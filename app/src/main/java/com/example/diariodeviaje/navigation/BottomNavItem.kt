package com.example.diariodeviaje.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector


sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {

    object Home : BottomNavItem(
        route = "home_screen",
        title = "Inicio",
        icon = Icons.Default.Home
    )

    object Record : BottomNavItem(
        route = "record_screen",
        title = "Grabar",
        icon = Icons.Default.PlayArrow
    )

    object Profile : BottomNavItem(
        route = "profile_screen",
        title = "Perfil",
        icon = Icons.Default.Person
    )
}