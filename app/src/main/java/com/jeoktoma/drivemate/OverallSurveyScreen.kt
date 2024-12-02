package com.jeoktoma.drivemate

import android.content.Context
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverallSurveyScreen(
    surveyViewModel: SurveyViewModel,
    context: Context,
    navController: NavController? = null
) {
    val surveyRequest = remember { mutableStateOf(OverallSurveyRequest(0, 0, 0, 0, 0, 0, "")) }

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
                    if (navController != null) {
                        navController.popBackStack()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = "설문",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    fontFamily = FontFamily(Font(R.font.freesentation))
                )
                if (navController != null) {
                    ThreeDotMenu(navController = navController) {
                        navController.navigate("loginScreen") {
                            popUpTo("loginScreen") { inclusive = true }
                        }
                    }
                }
            }
        },
        bottomBar = {
            Button(
                onClick = {
//                    surveyViewModel.submitOverallSurvey(
//                        surveyRequest.value,
//                        context
//                    ) {
//                        navController.navigate("reportScreen") {
//                            popUpTo("segmentSurveyScreen") { inclusive = true }
//                        }
//                    }
                    if (navController != null) {
                        surveyViewModel.switchLight = surveyRequest.value.switchLight
                        surveyViewModel.laneStaying = surveyRequest.value.laneStaying
                        surveyViewModel.sideMirror = surveyRequest.value.sideMirror
                        surveyViewModel.tension = surveyRequest.value.tension
                        surveyViewModel.weather = surveyRequest.value.weather
                        surveyViewModel.memo = surveyRequest.value.memo
                        navController.navigate("sightAdjustmentScreen")
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
                    Text(text = "Next", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.freesentation)))
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // 질문 리스트
            val questions = listOf(
                "차선 및 방향 전환 시 항상 깜빡이에 신경 썼나요?",
                "사이드미러를 잘 활용하였나요?",
                "운전 시 긴장도는 어느 정도였나요?",
                "날씨가 운전에 영향을 끼쳤나요?",
                "차선을 잘 유지하며 운전하였나요?"
            )
            itemsIndexed(questions) { index, question ->
                SurveyCheckboxGroup(
                    question = question,
                    selectedValue = { value ->
                        when (index) {
                            0 -> surveyRequest.value =
                                surveyRequest.value.copy(switchLight = value)
                            1 -> surveyRequest.value =
                                surveyRequest.value.copy(sideMirror = value)
                            2 -> surveyRequest.value =
                                surveyRequest.value.copy(tension = value)
                            3 -> surveyRequest.value =
                                surveyRequest.value.copy(weather = value)
                            4 -> surveyRequest.value =
                                surveyRequest.value.copy(laneStaying = value)
                        }
                    },
                    showDivider = index != questions.lastIndex
                )
            }

            // 메모 섹션
            item {
                Text(
                    text = "Memo",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    fontSize = 22.sp,
                    fontFamily = FontFamily(Font(R.font.freesentation)),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFF2F2F2),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        imageVector = Icons.Default.EditNote, // 메모 아이콘
                        contentDescription = "Memo Icon",
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = surveyRequest.value.memo,
                        onValueChange = {
                            surveyRequest.value = surveyRequest.value.copy(memo = it)
                        },
                        placeholder = { Text("한줄 메모로 주행 기록을 정리해보세요", fontSize = 16.sp, fontFamily = FontFamily(Font(R.font.freesentation))) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                    )
                }
            }
        }
    }
}


@Composable
fun SurveyCheckboxGroup(question: String, selectedValue: (Int) -> Unit, showDivider: Boolean) {
    val options = listOf(1, 2, 3, 4, 5) // 1 ~ 5 선택지
    val selectedOption = remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 질문 텍스트
        Text(
            text = question,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            fontFamily = FontFamily(Font(R.font.freesentation)),
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 선택지와 "전혀 아니다" 및 "매우 그렇다" 텍스트
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "전혀 아니다",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily(Font(R.font.freesentation)),
                fontSize = 16.sp
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                options.forEach { option ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (selectedOption.value == option) Color(0xFFC58BF2) else Color(
                                    0xFFE0E0E0
                                ),
                                shape = CircleShape
                            )
                            .clickable {
                                selectedOption.value = option
                                selectedValue(option)
                            }
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$option",
                            color = if (selectedOption.value == option) Color.White else Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Text(
                text = "매우 그렇다",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily(Font(R.font.freesentation)),
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Divider 추가 (필요할 때만)
        if (showDivider) {
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

@Composable
fun SightAdjustmentScreen(
    surveyViewModel: SurveyViewModel,
    context: Context,
    navController: NavController
) {
    var sliderValue by remember { mutableStateOf(0f) }
    var dragOffset by remember { mutableStateOf(0f) }
    var sightDegree by remember { mutableStateOf(0) } // 시야각 정수값

    val maxDragValue = 1000f // 드래그 최대값
    val blackBoxWidth = ((maxDragValue - dragOffset).coerceIn(0f, maxDragValue) / maxDragValue) * 100

    val backgroundImage: Painter = painterResource(id = R.drawable.sightdegree_img)

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
                    text = "설문",
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
                    surveyViewModel.sightDegree = sightDegree
                    surveyViewModel.submitOverallSurvey(
                        OverallSurveyRequest(surveyViewModel.switchLight,
                            surveyViewModel.sideMirror,
                            surveyViewModel.tension,
                            surveyViewModel.weather,
                            surveyViewModel.laneStaying,
                            surveyViewModel.sightDegree,
                            surveyViewModel.memo),
                        context
                    ) {
                        val reportRequest = DriveReportRequest(
                            startLocation = "출발지 예시",
                            endLocation = "도착지 예시",
                            startTime = "2024-11-15T13:00:00Z",
                            endTime = "2024-11-15T13:30:00Z"
                        )
                        surveyViewModel.submitDriveReport(reportRequest, context) {
                            navController.navigate("mainScreen")
                        }
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
                    Text(text = "Submit", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.freesentation)))
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            //verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "운행 시 평균 시야각은 어느 정도였나요?",
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                fontFamily = FontFamily(Font(R.font.freesentation)),
                fontSize = 20.sp
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    //.weight(0.1f)
                    .background(Color.Transparent)
            ) {
                // 배경 이미지
                Image(
                    painter = backgroundImage,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                // 왼쪽 검은색 박스
                Box(
                    modifier = Modifier
                        .height(165.dp)
                        .width(blackBoxWidth.dp)
                        .background(Color.Black.copy(alpha = 0.7f))
                        .align(Alignment.TopStart)
                )

                // 오른쪽 검은색 박스
                Box(
                    modifier = Modifier
                        .height(165.dp)
                        .width((blackBoxWidth*2).dp)
                        .background(Color.Black.copy(alpha = 0.7f))
                        .align(Alignment.TopEnd)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "드래그하여 시야각을 조절해주세요",
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(8.dp) // 여백 줄임
                    .align(Alignment.CenterHorizontally),
                //style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                fontFamily = FontFamily(Font(R.font.freesentation))
            )

            // 슬라이더
            Slider(
                value = sliderValue,
                onValueChange = {
                    sliderValue = it
                    dragOffset = (it * maxDragValue).coerceIn(0f, maxDragValue) // 슬라이더 값과 드래그 오프셋 연동
                    sightDegree = (it * 100).toInt() // 슬라이더 값(0.0~1.0)을 0~100 정수로 변환
                                },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFAA66CC),
                    activeTrackColor = Color(0xFFAA66CC)
                )
            )

            //submit
//            Button(
//                onClick = {
//                    val reportRequest = DriveReportRequest(
//                        startLocation = "출발지 예시",
//                        endLocation = "도착지 예시",
//                        startTime = "2024-11-15T13:00:00Z",
//                        endTime = "2024-11-15T13:30:00Z"
//                    )
//                    surveyViewModel.submitDriveReport(reportRequest, context) { driveId ->
//                        //navController.navigate("reportScreen/$driveId") {
//                          //  popUpTo("segmentSurveyScreen") { inclusive = true }
//                        //}
//                    }
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp, vertical = 16.dp)
//                    .height(56.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
//                contentPadding = PaddingValues()
//            ) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(
//                            Brush.linearGradient(
//                                colors = listOf(
//                                    Color(0xFF92A3FD),
//                                    Color(0xFF9DCEFF)
//                                )
//                            )
//                        ),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = "Submit",
//                        color = Color.White,
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.Bold,
//                        fontFamily = FontFamily(Font(R.font.freesentation))
//                    )
//                }
//            }

//            // 드래그 가능한 박스
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 16.dp)
//            ) {
//                // 배경 트랙
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(20.dp)
//                        .background(Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
//                        .align(Alignment.Center)
//                )
//                // 드래그 가능한 상자
//                Box(
//                    modifier = Modifier
//                        .width(40.dp)
//                        .height(20.dp)
//                        .background(
//                            Brush.linearGradient(
//                                colors = listOf(Color(0xFFC58BF2), Color(0xFFEEA4CE))
//                            ),
//                            RoundedCornerShape(2.dp)
//                        )
//                        .align(Alignment.CenterStart)
//                        .offset(x = dragOffset.dp)
//                        .draggable(
//                            orientation = androidx.compose.foundation.gestures.Orientation.Horizontal,
//                            state = rememberDraggableState { delta ->
//                                dragOffset = (dragOffset + delta).coerceIn(0f, maxDragValue)
//                                sliderValue = (dragOffset / maxDragValue).coerceIn(0f, 1f) // 드래그 오프셋과 슬라이더 값 연동
//                            }
//                        )
//                )
//            }

        }
    }
}

//@Preview(showBackground = true, widthDp = 412, heightDp = 917)
//@Composable
//fun PreviewOverallSurveyScreen() {
//    OverallSurveyScreen(
//        surveyViewModel = SurveyViewModel(),
//        context = androidx.compose.ui.platform.LocalContext.current,
//        navController = androidx.navigation.compose.rememberNavController()
//    )
//}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun PreviewSightAdjustmentScreen() {
    val context = LocalContext.current
    val dummySurveyViewModel = SurveyViewModel()
    val dummyNavController = rememberNavController()

    SightAdjustmentScreen(
        surveyViewModel = dummySurveyViewModel,
        context = context,
        navController = dummyNavController
    )
}
