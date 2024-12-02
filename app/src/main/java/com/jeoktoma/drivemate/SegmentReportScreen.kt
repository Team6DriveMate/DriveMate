package com.jeoktoma.drivemate

import android.util.Log
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
import com.google.accompanist.flowlayout.FlowRow
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.PathOverlay
import com.naver.maps.map.compose.rememberCameraPositionState


@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun SegmentReportScreen(
    reportId: Int,
//    totalSegments: Int,
//    surveyRequest: SegmentSurveyRequest, // 서버에서 가져온 데이터
    navController: NavController
) {
    val context = LocalContext.current
    var detailReport by remember { mutableStateOf<DetailReportResponse?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var segmentScreenIndex by remember { mutableStateOf(0) } // 현재 표시 중인 Segment Index



    LaunchedEffect(key1 = reportId) {
        isLoading = true
        detailReport = getReportDetail(reportId, context)
        isLoading = false
    }

    val totalSegments = detailReport?.segmentSurveys?.size ?: 0
    Log.d("segmentreportscreen", "reportID = ${reportId}")
    Log.d("segmentreportscreen", "${detailReport}")

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    if (segmentScreenIndex > 0) {
                        segmentScreenIndex -= 1 // 이전 Segment로 이동
                    } else {
                        navController.popBackStack() // 이전 화면으로 이동
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = "구간 리포트 (${segmentScreenIndex + 1}/${totalSegments})",
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
        bottomBar = {
            Button(
                onClick = {
                    if (segmentScreenIndex < totalSegments - 1) {
                        segmentScreenIndex += 1 // 다음 Segment로 이동
                    } else {
                        navController.navigate("overallReportScreen/${reportId}") // 전체 리포트 화면으로 이동
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
                val currentSegment = report.segmentSurveys.getOrNull(segmentScreenIndex)
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
                        val segment = report.path.route.segments[currentSegment!!.segmentIndex]
                        val cameraPositionState:CameraPositionState = rememberCameraPositionState{
                            position = CameraPosition(
                                LatLng((segment.startPoint.lat + segment.endPoint.lat)/2,
                                (segment.startPoint.lng + segment.endPoint.lng)/2), 11.0)
                        }

                        var mapUiSettings by remember {
                            mutableStateOf(
                                MapUiSettings(isScrollGesturesEnabled = false,
                                    isZoomGesturesEnabled = false,
                                    isTiltGesturesEnabled = false,
                                    isRotateGesturesEnabled = false,
                                    isLocationButtonEnabled = false,
                                    isCompassEnabled = false,
                                    isScaleBarEnabled = false,
                                    isZoomControlEnabled = false
                                    )
                            )
                        }

                        NaverMap(modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            uiSettings = mapUiSettings,
                            onMapLoaded = {
                                cameraPositionState.move(CameraUpdate.fitBounds(
                                    LatLngBounds.Builder()
                                        .include(LatLng(segment.startPoint.lat, segment.startPoint.lng))
                                        .include(LatLng(segment.endPoint.lat, segment.endPoint.lng))
                                        .build(), 100))
                            }
                        ) {
                            PathOverlay(
                                coords = segment.path.map { LatLng(it.lat, it.lng) },
                                color = when(segment.traffic){
                                    "1" -> Color.Green  // 원활
                                    "2" -> Color.Yellow // 서행
                                    "3" -> Color(0xFFFFA500)// 지체
                                    "4" -> Color.Red   // 정체
                                    else -> Color.Gray // 알 수 없음
                                    },
                                width = 10.dp, // 경로의 두께 설정
                                outlineColor = Color.Black,
                                outlineWidth = 2.dp
                            )

                        }
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
                            val issues = listOf(
                                "차선 변경" to segment.laneSwitch,
                                "판단 미숙" to segment.situationDecision,
                                "차선 혼동" to segment.laneConfusion,
                                "도로 규범 준수" to segment.trafficLaws,
                                "긴장" to segment.tension
                            ).filter { it.second }

                            if (issues.any { it.second }) {
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
                                        // 문제들을 버튼으로 표시
                                        FlowRow(
                                            modifier = Modifier.fillMaxWidth(),
                                            mainAxisSpacing = 8.dp, // 버튼 간의 가로 간격
                                            crossAxisSpacing = 8.dp // 버튼 간의 세로 간격
                                        )  {
                                            issues.forEach { (issue, isActive) ->
                                                StaticSurveyButton(
                                                    label = issue,
                                                    isSelected = isActive
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
}

// 비활성화된 SurveyButton
@Composable
fun StaticSurveyButton(label: String, isSelected: Boolean) {
    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .background(
                brush = if (isSelected) Brush.linearGradient(
                    colors = listOf(Color(0xFFC58BF2), Color(0xFFEEA4CE))
                ) else Brush.linearGradient(
                    colors = listOf(Color(0xFFDDDADA), Color(0xFFDDDADA))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 8.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else Color.Black,
            fontFamily = FontFamily(Font(R.font.freesentation)),
            fontSize = 18.sp
        )
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
//        segmentScreenIndex = 0,
//        totalSegments = 3,
//        surveyRequest = dummySurveyRequest,
//        navController = dummyNavController // 프리뷰용 NavController
//    )
//}
