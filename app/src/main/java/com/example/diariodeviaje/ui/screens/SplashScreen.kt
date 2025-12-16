package com.example.diariodeviaje.ui.screens

import android.window.SplashScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.diariodeviaje.navigation.AppScreen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController){
    LaunchedEffect(key1 = true){
        delay(2000L)
        navController.navigate(AppScreen.Login.route){
            popUpTo(AppScreen.Splash.route){
                inclusive = true
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(
            "B-Trax",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )
    }
}