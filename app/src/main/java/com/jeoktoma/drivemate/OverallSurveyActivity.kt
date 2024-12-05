//package com.jeoktoma.drivemate
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.platform.LocalContext
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//
//class OverallSurveyActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            val navController = rememberNavController()
//            NavigationGraph(navController = navController)
//        }
//    }
//}
//
//@Composable
//fun NavigationGraph(navController: NavHostController) {
//    val context = LocalContext.current
//
//    NavHost(
//        navController = navController,
//        startDestination = "overallSurveyScreen"
//    ) {
//        composable("overallSurveyScreen") {
//            OverallSurveyScreen(
//                surveyViewModel = SurveyViewModel(),
//                context = context,
//                navController = navController
//            )
//        }
//        composable("sightAdjustmentScreen") {
//            SightAdjustmentScreen(
//                surveyViewModel = SurveyViewModel(),
//                context = context,
//                navController = navController
//            )
//        }
//    }
//}
//
