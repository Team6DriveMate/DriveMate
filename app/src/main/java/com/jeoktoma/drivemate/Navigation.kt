package com.jeoktoma.drivemate

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun AppNavigation(navController: NavHostController) {
    val selectedItem = remember { mutableStateOf(0) }
    val sharedViewModel: UserViewModel = remember { UserViewModel() }

    NavHost(navController = navController, startDestination = "loginScreen") {
        composable("loginScreen") {
            LoginScreen(navController, sharedViewModel, context = LocalContext.current)
        }
        composable("signUpScreen") {
            SignUpScreen(navController, sharedViewModel, context = LocalContext.current)
        }
        composable("mainScreen") {
            selectedItem.value = 0
            MainScreen(navController, sharedViewModel, selectedItem)
        }
        composable("pathScreen") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                val intent = Intent(context, NavActivity::class.java)
                // 필요한 데이터를 전달
                intent.putExtra("key", "value")
                context.startActivity(intent)
            }
        }
        composable("reportScreen") {
            selectedItem.value = 1
            ReportScreen(navController, selectedItem)
        }

        composable(
            route = "segmentReportScreen/{reportId}",
            arguments = listOf(
                navArgument("reportId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getInt("reportId") ?: 0

            val reportViewModel: ReportViewModel = viewModel()

            SegmentReportScreen(
                reportId = reportId,
//                totalSegments = totalSegments,
//                surveyRequest = surveyRequest,
                navController = navController
            )
        }

        composable("overallReportScreen") {
            OverallReportScreen(navController = navController)
        }

        composable("tipScreen") {
            selectedItem.value = 3
            TipScreen(navController, selectedItem)
        }
//        composable("profileScreen") {
//            selectedItem.value = 4
//            ProfileScreen(navController, selectedItem)
//        }
        composable("profileScreen") {
            selectedItem.value = 4
            ProfileScreen(navController = navController, selectedItem, sharedViewModel)
        }

        composable("editProfileScreen") {
            selectedItem.value = 4
            EditProfileScreen(navController, selectedItem, sharedViewModel)
        }

        composable(
            route = "tipDetailsScreen/{tipTitle}/{tipDetailsJson}",
            arguments = listOf(
                navArgument("tipTitle") { type = NavType.StringType },
                navArgument("tipDetailsJson") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tipTitle = backStackEntry.arguments?.getString("tipTitle") ?: "Unknown"
            val tipDetailsJson = backStackEntry.arguments?.getString("tipDetailsJson") ?: "[]"
            val tipDetails = Gson().fromJson<List<TipDetail>>(Uri.decode(tipDetailsJson), object : TypeToken<List<TipDetail>>() {}.type)
            selectedItem.value = 3
            TipDetailsScreen(navController = navController, tipTitle = tipTitle, tips = tipDetails, selectedItem)
        }

//        composable(
//            route = "segmentSurveyScreen/{roadName}/{segmentIndex}/{totalSegments}",
//            arguments = listOf(
//                navArgument("roadName") { type = NavType.StringType },
//                navArgument("segmentIndex") { type = NavType.IntType },
//                navArgument("totalSegments") { type = NavType.IntType }
//            )
//        ) { backStackEntry ->
//            val roadName = backStackEntry.arguments?.getString("roadName") ?: "Unknown Road"
//            val segmentIndex = backStackEntry.arguments?.getInt("segmentIndex") ?: 0
//            val totalSegments = backStackEntry.arguments?.getInt("totalSegments") ?: 1
//            val context = LocalContext.current
//
//            val surveyViewModel: SurveyViewModel = viewModel()
//
//            SegmentSurveyScreen(
//                roadName = roadName,
//                segmentIndex = segmentIndex,
//                totalSegments = totalSegments,
//                surveyViewModel = surveyViewModel, // ViewModel 주입
//                context = context,
//
//                navController = navController
//            )
//        }


        composable("overallSurveyScreen") {
            val context = LocalContext.current
            OverallSurveyScreen(
                surveyViewModel = SurveyViewModel(), // ViewModel 생성
                context = context,
                navController = navController
            )
        }

        composable("sightAdjustmentScreen") {
            val context = LocalContext.current
            SightAdjustmentScreen(
                surveyViewModel = SurveyViewModel(), // 동일한 ViewModel 사용
                context = context,
                navController = navController
            )
        }


    }
}