//package com.jeoktoma.drivemate
//
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.navigation.NavController
//import com.google.relay.compose.BoxScopeInstance.columnWeight
//import com.google.relay.compose.BoxScopeInstance.rowWeight
//import com.jeoktoma.drivemate.tipmainscreen.TipMainScreen
//
//@Composable
//fun TipScreen(navController: NavController){
//    TipMainScreen(
//        backNavsTap = {
//            navController.popBackStack()
//        },
//        homeTap = {},
//        tipTap = {},
//        reportTap = {},
//        profileTap = {},
//        searchTap = {},
//        buttonParkingViewmore = {
//                                //navController.navigate()
//        },
//        buttonHighwayViewmore = {},
//        buttonCheckingViewmore = {},
//        buttonAccidentViewmore = {},
//        buttonMannerViewmore = {},
//        modifier = Modifier.rowWeight(1.0f).columnWeight(1.0f)
//    )
//}