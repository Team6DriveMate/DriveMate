package com.jeoktoma.drivemate

import android.content.Context
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun OverallReportScreen(
    reportId: Int,
    navController: NavController,
    context: Context
) {
    val context = LocalContext.current
    var detailReport by remember { mutableStateOf<DetailReportResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // 데이터 로드
    LaunchedEffect(key1 = reportId) {
        isLoading = true
        detailReport = getReportDetail(reportId, context)
        isLoading = false
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
                    text = "리포트",
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
                    val sightDegree = 80
                    navController.navigate("sightAdjustmentReportScreen/${sightDegree}")
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
                    Text(text = "Next", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily(
                        Font(R.font.freesentation)
                    )
                    )
                }
            }
        },
    content = { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("로딩 중...")
            }
        } else {
            detailReport?.let { report ->
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
                    val responses = report.overallSurvey

                    itemsIndexed(questions) { index, question ->
                        OverallReportResponseGroup(
                            question = question,
                            response = when (index) {
                                0 -> responses.switchLight
                                1 -> responses.sideMirror
                                2 -> responses.tension
                                3 -> responses.weather
                                4 -> responses.laneStaying
                                else -> 0
                            },
                            showDivider = index != questions.lastIndex // 마지막 아이템이 아닐 때 Divider 표시
                        )
                    }

                    // 메모 섹션
                    item {
                        Text(
                            text = "Memo",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            fontSize = 24.sp,
                            fontFamily = FontFamily(Font(R.font.freesentation)),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFFF2F2F2),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp),
                            contentAlignment = Alignment.TopStart
                        ) {
                            Text(
                                text = report.overallSurvey.memo,
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = FontFamily(Font(R.font.freesentation)),
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

            } ?: Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("리포트를 불러오지 못했습니다.")
            }
        }
    }
    )
}

@Composable
fun OverallReportResponseGroup(question: String, response: Int, showDivider: Boolean) {
    val responseColors = listOf(
        Color(0xFFE64969), // 전혀 아니다
        Color(0xFFEEA4CE), // 아니다
        Color(0xFFB0CEE8), // 보통이다
        Color(0xFF9DCEFF), // 그렇다
        Color(0xFF94AAFD)  // 매우 그렇다
    )

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

        // 응답 버튼 표시
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
                (1..5).forEach { option ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = if (response == option) responseColors[option - 1] else Color(
                                    0xFFE0E0E0
                                ),
                                shape = CircleShape
                            )
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "",
                            color = if (response == option) Color.White else Color.Black,
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
fun SightAdjustmentReportScreen(
    sightDegree: Int, // 0 ~ 100 값
    navController: NavController,
    context: Context
) {
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
                    text = "리포트",
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
                    CoroutineScope(Dispatchers.Main).launch {
                        val success = performReadReport(context)
                    }
                    navController.navigate("reportScreen"){
                        popUpTo("reportScreen"){inclusive = true}
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
                        text = "확인",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.freesentation))
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "운행 평균 시야각",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                fontFamily = FontFamily(Font(R.font.freesentation)),
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) {
                Image(
                    painter = backgroundImage,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth()
                )

                // 왼쪽 검은색 박스
                Box(
                    modifier = Modifier
                        .height(165.dp)
                        .width(sightDegree.dp)
                        .background(Color.Black.copy(alpha = 0.7f))
                        .align(Alignment.TopStart)
                )

                Box(
                    modifier = Modifier
                        .height(165.dp)
                        .width((sightDegree * 2).dp)
                        .background(Color.Black.copy(alpha = 0.7f))
                        .align(Alignment.TopEnd)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
