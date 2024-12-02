package com.jeoktoma.drivemate

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ReportScreen(navController: NavController, selectedItem: MutableState<Int>) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val surveyViewModel: SurveyViewModel = viewModel() // ViewModel 선언

    // API 호출 결과 상태 관리
    var completeResponse by remember { mutableStateOf<CompleteResponse?>(null) }
//    var isLoading by remember { mutableStateOf(false) }

    // 좌표 데이터 (37.5050, 126.9539로 고정)
    val startLocation = Point(37.5050, 126.9539)
    val endLocation = Point(37.5050, 126.9539)
    val stopoverLocations: List<Point>? = null

    // API 호출 결과 상태 관리
    var reports by remember { mutableStateOf<List<Report>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // API 호출
    LaunchedEffect(key1 = Unit) {
        scope.launch {
            val response = getReportList(context)
            if (response != null) {
                reports = response.reports
            }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = "운행 리포트",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    fontFamily = FontFamily(Font(R.font.freesentation))
                )
                ThreeDotMenu(navController = navController) {
                    navController.navigate("loginScreen") {
                        popUpTo("loginScreen") { inclusive = true }
                    }
                }
            }
        },
        bottomBar = { BottomNavigationBar(navController, selectedItem) }
    ) { padding ->


//        performCompleteService(Point(37.5050, 126.9539), Point(37.5666, 126.9782),
//            null, context)

        //37.5050, 126.9539

        if (isLoading) {
            // 로딩 중 표시
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("로딩 중...")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                itemsIndexed(reports) { index, report ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        ReportItem(report, onClick = {
                            Log.d("reportscreen", "reportID = ${report.reportId}")
                            navController.navigate("segmentReportScreen/${report.reportId}")
                        })

                        if (index < reports.lastIndex) {
                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                color = Color(0xFFE0E0E0),
                                thickness = 1.dp
                            )
                        }
                    }
                }
            }
        }

        Button(onClick = {
//            navController.navigate("segmentSurveyScreen/1/0/3")
            CoroutineScope(Dispatchers.Main).launch {
                val completeResponse = performCompleteService(
                    Point(37.5050, 126.9539), // 시작 좌표
                    Point(37.5666, 126.9782), // 종료 좌표
                    null, // 경유지 좌표 없음
                    context // 컨텍스트
                )

                if (completeResponse != null) {
                    // 서버에서 받은 응답 확인 (디버깅용 로그 추가)
                    Log.d("SegmentSurveyScreen", "Complete Response: $completeResponse")
                } else {
                    Log.e("SegmentSurveyScreen", "Complete 호출 실패")
                }
            }

            CoroutineScope(Dispatchers.Main).launch {
                val intent = Intent(context, SurveyActivity::class.java)
                context.startActivity(intent)
            }
        }) {
            Text(text = "SegmentSurveyScreen")
        }

//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(onClick = {  navController.navigate("overallSurveyScreen") }) {
//        Text(text = "OverallSurveyScreen")
//        }
    }

}

@Composable
fun ReportItem(report: Report, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = report.title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                fontFamily = FontFamily(Font(R.font.freesentation)),
                fontSize = 18.sp
            )
            Text(
                text = report.date + "  " + report.time,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                fontFamily = FontFamily(Font(R.font.freesentation)),
                fontSize = 14.sp
            )
        }
    }
}

data class ReportData(val title: String, val date: String)