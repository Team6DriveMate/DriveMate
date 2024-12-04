package com.jeoktoma.drivemate

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.overlay.PathOverlay
import android.graphics.Color as PathColor

@Composable
fun SegmentSurveyScreen(
    roadName: String,
    segmentIndex: Int,
    totalSegments: Int,
    surveyViewModel: SurveyViewModel,
    context: Context,
    navController: NavController? = null,
    segmentCoords: List<LatLng>,
    traffic: String,
    roadType: String,
    onBackSurvey: () -> Unit,
    onExitSurvey: () -> Unit // 추가
) {
    val surveyRequest = remember {
        mutableStateOf(
            SegmentSurveyRequest(
                sectionName = roadName,
                trafficCongestion = false,
                roadType = false,
                laneSwitch = false,
                situationDecision = false,
                laneConfusion = false,
                trafficLaws = false,
                tension = false
            )
        )
    }

//    LaunchedEffect(Unit) {
//        val completeResponse = performCompleteService(
//            Point(37.5050, 126.9539), // 시작 좌표
//            Point(37.5666, 126.9782), // 종료 좌표
//            null, // 경유지 좌표 없음
//            context // 컨텍스트
//        )
//
//        if (completeResponse != null) {
//            // 서버에서 받은 응답 확인 (디버깅용 로그 추가)
//            Log.d("SegmentSurveyScreen", "Complete Response: $completeResponse")
//        } else {
//            Log.e("SegmentSurveyScreen", "Complete 호출 실패")
//        }
//    }
//
//    LaunchedEffect(Unit) {
//        val intent = android.content.Intent(context, SurveyActivity::class.java)
//        context.startActivity(intent)
//    }

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
                    onBackSurvey()
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = "설문 - $roadName",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    fontFamily = FontFamily(Font(R.font.freesentation))
                )

                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = Color.White
                )
            }
        },
        bottomBar = {
            Button(
                onClick = {
                    surveyViewModel.submitSegmentSurvey(
                        //segmentIndex,
                        1,
                        surveyRequest.value,
                        context
                    ) {
//                        // ComposeView를 숨기고 SurveyActivity에서 showFullRoute 호출
//                        if (context is SurveyActivity) {
//                            Log.d("1","1")
//                            val composeContainer = (context as SurveyActivity).findViewById<FrameLayout>(R.id.compose_container)
//                            composeContainer.visibility = View.GONE
//                            Log.d("1", "${context.getRouteResponse}")
//                            context.showFullRoute(context.getRouteResponse) // 전체 경로 화면으로 돌아감
//                        }
                        onExitSurvey()
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
                    // 구간 설문 저장하고 제출
                    // 전으로 돌아가기
                    Text(text = "구간 설문 저장", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.freesentation)))
                }
            }

        }
    ) { innerPadding ->

//        CoroutineScope(Dispatchers.Main).launch {
//            val completeResponse = performCompleteService(
//                Point(37.5050, 126.9539), Point(37.5666, 126.9782),
//                null, context
//            )
//        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            // 지도
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(16.dp))
            ) {
                AndroidView(
                    factory = { context ->
                        MapView(context).apply {
                            getMapAsync { nmap ->
                                val pathOverlay = PathOverlay().apply {
                                    coords = segmentCoords
                                    color = PathColor.RED
                                    width = 10
                                    outlineWidth = 5
                                    map = nmap
                                }
                                val bounds = LatLngBounds.Builder().include(segmentCoords).build()
                                nmap.moveCamera(CameraUpdate.fitBounds(bounds, 100))
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 설문 질문
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White) // 배경색 지정
            ) {
                QuestionToggle(
                    title = "교통 상황은 ${traffic} 상태였습니다",
                    description = "교통 혼잡도에 문제가 있었나요?",
                    isChecked = surveyRequest.value.trafficCongestion,
                    onCheckedChange = { isChecked ->
                        surveyRequest.value = surveyRequest.value.copy(trafficCongestion = isChecked)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                QuestionToggle(
                    title = "해당 도로는 ${roadType} 타입이었습니다",
                    description = "${roadType} 도로를 이용하는데 어려움이 있었나요?",
                    isChecked = surveyRequest.value.roadType,
                    onCheckedChange = { isChecked ->
                        surveyRequest.value = surveyRequest.value.copy(roadType = isChecked)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 버튼 설문
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    mainAxisSpacing = 4.dp,
                    crossAxisSpacing = 8.dp,
                    mainAxisAlignment = FlowMainAxisAlignment.Center
                ) {
                    SurveyButton(
                        label = "차선 변경",
                        isSelected = surveyRequest.value.laneSwitch,
                        onSelectedChange = { isSelected ->
                            surveyRequest.value = surveyRequest.value.copy(laneSwitch = isSelected)
                        }
                    )
                    SurveyButton(
                        label = "판단 미숙",
                        isSelected = surveyRequest.value.situationDecision,
                        onSelectedChange = { isSelected ->
                            surveyRequest.value = surveyRequest.value.copy(situationDecision = isSelected)
                        }
                    )
                    SurveyButton(
                        label = "차선 혼동",
                        isSelected = surveyRequest.value.laneConfusion,
                        onSelectedChange = { isSelected ->
                            surveyRequest.value = surveyRequest.value.copy(laneConfusion = isSelected)
                        }
                    )
                    SurveyButton(
                        label = "도로 규범 준수",
                        isSelected = surveyRequest.value.trafficLaws,
                        onSelectedChange = { isSelected ->
                            surveyRequest.value = surveyRequest.value.copy(trafficLaws = isSelected)
                        }
                    )
                    SurveyButton(
                        label = "긴장",
                        isSelected = surveyRequest.value.tension,
                        onSelectedChange = { isSelected ->
                            surveyRequest.value = surveyRequest.value.copy(tension = isSelected)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun QuestionToggle(title: String, description: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF2F2F2),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 12.dp, horizontal = 12.dp) // 배경과 내용 간 간격
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            fontFamily = FontFamily(Font(R.font.freesentation)),
            fontSize = 19.sp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                fontFamily = FontFamily(Font(R.font.freesentation)),
                fontSize = 17.sp
            )
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = androidx.compose.material3.SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFFEEA4CE),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }
    }
}

@Composable
fun SurveyButton(label: String, isSelected: Boolean, onSelectedChange: (Boolean) -> Unit) {
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
            .clickable { onSelectedChange(!isSelected) }
            .padding(vertical = 8.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else Color.Black,
            fontFamily = FontFamily(Font(R.font.freesentation)),
            fontSize = 20.sp
        )
    }
}
