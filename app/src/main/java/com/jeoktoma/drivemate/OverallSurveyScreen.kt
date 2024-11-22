package com.jeoktoma.drivemate

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverallSurveyScreen(
    onComplete: () -> Unit,
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
                onClick = { onComplete()
                    navController.navigate("reportScreen") {
                        popUpTo("segmentSurveyScreen") { inclusive = true }
                    } },
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // 질문 리스트
            val questions = listOf(
                "차선 및 방향 전환 시 항상 깜빡이에 신경 썼나요?",
                "사이드미러를 잘 활용하였나요?",
                "운전 시 긴장도는 어느 정도였나요?",
                "날씨가 운전에 영향을 끼쳤나요?",
                "차선을 잘 유지하며 운전하였나요?"
            )
            items(questions) { question ->
                SurveySlider(question)
            }

            // 메모 섹션
            item {
                //Spacer(modifier = Modifier.height(16.dp))
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
                        value = "",
                        onValueChange = {},
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
fun SurveySlider(question: String) {
    val sliderValue = remember { mutableStateOf(0.5f) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            //.padding(vertical = 8.dp)
    ) {
        Text(
            text = question,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            fontFamily = FontFamily(Font(R.font.freesentation)),
            fontSize = 20.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "전혀 아니다", style = MaterialTheme.typography.bodySmall, fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.freesentation)))
            Text(text = "매우 그렇다", style = MaterialTheme.typography.bodySmall, fontSize = 18.sp, fontFamily = FontFamily(Font(R.font.freesentation)))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
                    .align(Alignment.Center)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(sliderValue.value)
                    .height(4.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFC58BF2), Color(0xFFEEA4CE))
                        ),
                        RoundedCornerShape(2.dp)
                    )
                    .align(Alignment.CenterStart)
            )

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .offset(x = (sliderValue.value * (LocalConfiguration.current.screenWidthDp - 40)).dp - 10.dp)
                    .background(Color(0xFFC58BF2), CircleShape)
                    .align(Alignment.CenterStart)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .pointerInput(key1 = Unit) {
                        detectDragGestures { change, _ ->
                            change.consumeAllChanges()
                            val newValue = (change.position.x / size.width).coerceIn(0f, 1f)
                            val step = 0.25f
                            sliderValue.value = (newValue / step).roundToInt() * step
                        }
                    }
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 917)
@Composable
fun PreviewOverallSurveyScreen() {
    OverallSurveyScreen(
        onComplete = { },
        navController = NavController(LocalContext.current)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSurveySlider() {
    SurveySlider(question = "운전 시 긴장도는 어느 정도였나요?")
}