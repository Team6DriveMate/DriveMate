package com.jeoktoma.drivemate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SegmentReportScreen(
    reportId: Int,
//    totalSegments: Int,
//    surveyRequest: SegmentSurveyRequest, // 서버에서 가져온 데이터
    navController: NavController
) {
    val context = LocalContext.current
    var detailReport by remember { mutableStateOf<DetailReportResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var segmentIndex by remember { mutableStateOf(0) } // 현재 표시 중인 Segment Index



    LaunchedEffect(key1 = reportId) {
        isLoading = true
        detailReport = getReportDetail(reportId, context)
        isLoading = false
    }

    val totalSegments = detailReport?.segmentSurveys?.size?:0

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
                    if (segmentIndex > 0) {
                        segmentIndex -= 1 // 이전 Segment로 이동
                    } else {
                        navController.popBackStack() // 이전 화면으로 이동
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = "구간 리포트 (${segmentIndex + 1}/$totalSegments)",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    fontFamily = FontFamily(Font(R.font.freesentation))
                )
                IconButton(onClick = { /* 점 세 개 버튼 로직 (미정) */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More"
                    )
                }
            }
        },
        bottomBar = {
            Button(
                onClick = {
                    if (segmentIndex < totalSegments - 1) {
                        segmentIndex += 1 // 다음 Segment로 이동
                    } else {
                        navController.navigate("overallReportScreen") // 전체 리포트 화면으로 이동
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF92A3FD),
                                    Color(0xFF9DCEFF)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Next",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.freesentation))
                    )
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("로딩 중...")
            }
        } else {
            detailReport?.let { report ->
                val currentSegment = report.segmentSurveys.getOrNull(segmentIndex)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    // 지도 placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)
                            .background(Color.LightGray, shape = RoundedCornerShape(16.dp))
                    ) {
                        Text(
                            text = "지도 placeholder",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 설문 결과
                    currentSegment?.let { segment ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // 문제점 강조
                            if (segment.trafficCongestion || segment.roadType) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = Color(0xFFF2F2F2),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(16.dp)
                                ) {
                                    Column {
                                        if (segment.trafficCongestion) {
                                            Text(
                                                text = "교통 혼잡도로 인해 이 구간에서 문제를 겪었습니다.",
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                fontFamily = FontFamily(Font(R.font.freesentation)),
                                                fontSize = 18.sp
                                            )
                                        }
                                        if (segment.roadType) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "${segment.segmentName} 도로에서 어려움을 느꼈습니다.",
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                fontFamily = FontFamily(Font(R.font.freesentation)),
                                                fontSize = 18.sp
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 세부 문제 표시
                            val issues = mutableListOf<String>().apply {
                                if (segment.laneSwitch) add("차선 변경")
                                if (segment.situationDecision) add("판단 미숙")
                                if (segment.laneConfusion) add("차선 혼동")
                                if (segment.trafficLaws) add("도로 규범 준수")
                                if (segment.tension) add("긴장")
                            }

                            if (issues.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = Color(0xFFF2F2F2),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(16.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "이 구간에서 다음과 같은 문제를 겪으셨네요!",
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            fontFamily = FontFamily(Font(R.font.freesentation)),
                                            fontSize = 18.sp
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        issues.forEach { issue ->
                                            Text(
                                                text = issue,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = Color.Gray
                                                ),
                                                fontFamily = FontFamily(Font(R.font.freesentation)),
                                                fontSize = 16.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

//
//@Preview(showBackground = true, widthDp = 412, heightDp = 917)
//@Composable
//fun PreviewSegmentReportScreen() {
//    val dummySurveyRequest = SegmentSurveyRequest(
//        sectionName = "구간 1",
//        trafficCongestion = true,
//        roadType = false,
//        laneSwitch = true,
//        situationDecision = false,
//        laneConfusion = true,
//        trafficLaws = false,
//        tension = true
//    )
//    val dummyNavController = object : NavController(LocalContext.current) {}
//
//    SegmentReportScreen(
//        segmentIndex = 0,
//        totalSegments = 3,
//        surveyRequest = dummySurveyRequest,
//        navController = dummyNavController // 프리뷰용 NavController
//    )
//}
