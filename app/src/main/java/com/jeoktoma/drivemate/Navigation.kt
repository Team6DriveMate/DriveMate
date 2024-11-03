package com.jeoktoma.drivemate

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "loginScreen") {
        composable("loginScreen") { LoginScreen(navController) }
        composable("signUpScreen") { SignUpScreen(navController) }
        composable("mainScreen") { MainScreen(navController) }
        composable("pathScreen") { PathScreen(navController) }
        composable("driveScreen") { DriveScreen(navController) }
        composable("surveyScreen") { SurveyScreen(navController) }
        composable("tipScreen") { TipScreen(navController) }

    }
}