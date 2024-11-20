package com.jeoktoma.drivemate

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow

@Composable
fun SegmentSurveyScreen(
    segmentIndex: Int,
    totalSegments: Int,
    segmentImage: Int?, // 이미지 리소스 ID
    estimatedTime: String,
    actualTime: String,
    onNext: () -> Unit,
    navController: NavController
) {
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
                    text = "설문 (${segmentIndex + 1}/$totalSegments)",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
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
//            Button(
//                onClick = { onNext() },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//                    .height(48.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xFF92A3FD)
//                ),
//                shape = RoundedCornerShape(24.dp)
//            ) {
//                Text(text = "Next", color = Color.White)
//            }


            Button(
                onClick = { onNext() },
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
                    Text(text = "Next", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 구간 이미지 및 시간 정보
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(16.dp))
                ) {
                    // 이미지가 제공되었을 경우만 표시
                    segmentImage?.let {
                        Image(
                            painter = painterResource(id = it),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                //Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "소요시간: $actualTime", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "예상시간: $estimatedTime", style = MaterialTheme.typography.bodyLarge)
                }
            }

            // 설문 질문
            Column {
                QuestionToggle(title = "교통 혼잡도가 ‘정체’로 높았습니다", description = "높은 교통 혼잡도에 문제가 있었나요?")
                Spacer(modifier = Modifier.height(16.dp))
                QuestionToggle(title = "해당 도로의 타입은 ‘‘ 이었습니다", description = "‘’가 익숙하지 않았나요?")
                Spacer(modifier = Modifier.height(16.dp))

                // 버튼 설문
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    mainAxisSpacing = 4.dp,
                    crossAxisSpacing = 8.dp,
                    mainAxisAlignment = FlowMainAxisAlignment.Center
                ) {
                    SurveyButton("차선 변경")
                    SurveyButton("판단 미숙")
                    SurveyButton("차선 혼동")
                    SurveyButton("도로 규범 준수")
                    SurveyButton("긴장")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun QuestionToggle(title: String, description: String) {
    val isChecked = remember { mutableStateOf(false) }
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
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            //verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = description, style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray))
            Switch(
                checked = isChecked.value,
                onCheckedChange = { isChecked.value = it }, // 상태 업데이트
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
fun SurveyButton(label: String) {
    val selected = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            //.height(48.dp)
            //.width(120.dp)
            .background(
                brush = if (selected.value) Brush.linearGradient(
                    colors = listOf(Color(0xFFC58BF2), Color(0xFFEEA4CE))
                ) else Brush.linearGradient(
                    colors = listOf(Color(0xFFDDDADA), Color(0xFFDDDADA))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { selected.value = !selected.value }
            .padding(vertical = 8.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = if (selected.value) Color.White else Color.Black)
    }
}
