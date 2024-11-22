package com.jeoktoma.drivemate

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ReportScreen(navController: NavController, selectedItem: MutableState<Int>) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController, selectedItem) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("운행 리포트 화면", fontSize = 24.sp)
        }

        Column {
            Button(onClick = { navController.navigate("segmentSurveyScreen/0/3/15 min/14 min") }) {
                Text(text = "SegmentSurveyScreen")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navController.navigate("overallSurveyScreen") }) {
                Text(text = "OverallSurveyScreen")
            }
        }

    }
}

