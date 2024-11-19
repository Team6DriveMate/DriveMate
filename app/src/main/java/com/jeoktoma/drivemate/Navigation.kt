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
        composable("mainScreen") { MainScreen(navController, "코너링이 훌룡하시네요,", "Gildong Hong") }
        composable("pathScreen") { PathScreen(navController) }
        composable("reportScreen") { ReportScreen(navController) }
        composable("tipScreen") { TipScreen(navController) }
        composable("profileScreen") { ProfileScreen(navController) }
        composable("editProfileScreen") {EditProfileScreen(navController)}
    }
}