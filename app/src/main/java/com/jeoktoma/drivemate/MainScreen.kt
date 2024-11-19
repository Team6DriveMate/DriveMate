package com.jeoktoma.drivemate

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

//길찾기(path), 운행(drive), 설문(survey), 리마인드(tip), 사용자 프로필(이름, 칭호, 레벨 등 유저 정보)
@Composable
fun MainScreen(navController: NavController, title: String, userName: String) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController, 0) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            // 사용자 칭호와 이름
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp)
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            )

            Spacer(modifier = Modifier.height(36.dp))

            // 오늘의 꿀팁 카드
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF92A3FD), Color(0xFF9DCEFF))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
            ) {
                // 배경 그래픽 효과를 Canvas로 그리기
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawBackgroundShapes()
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "오늘의 꿀팁",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 20.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.poppins)),
                        )
                    )
                    Text(
                        text = "주차가 아직도 어렵다면?",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.poppins)),
                        )
                    )
                    Button(
                        onClick = { navController.navigate("tipScreen") },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Color(0xFFC58BF2), Color(0xFFEEA4CE))
                                    ),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                        ) {
                            Text(text = "더보기", color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // 지난 운행 박스
            Column {
                Text(
                    text = "지난 운행",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(
                            color = Color(0xFFF5F5F5),
                            shape = RoundedCornerShape(16.dp)
                        )
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, selectedIndex: Int) {
    val items = listOf("Home", "Report", "Path", "Tip", "Profile")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Outlined.Article,
        Icons.Default.Search,
        Icons.Default.Check,
        Icons.Default.Person
    )
    var selectedItem = remember { mutableStateOf(0) }

    NavigationBar(
        containerColor = Color(0xFFF7F8F8)
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem.value == index,
                onClick = {
                    selectedItem.value = index
                    when (item) {
                        "Home" -> navController.navigate("mainScreen")
                        "Report" -> navController.navigate("reportScreen")
                        "Path" -> navController.navigate("pathScreen")
                        "Tip" -> navController.navigate("tipScreen")
                        "Profile" -> navController.navigate("profileScreen")
                    }
                },
                icon = {
                    Icon(
                        imageVector = icons[index],
                        contentDescription = item,
                        tint = if (selectedItem.value == index) Color(0xFF8E44AD) // 선택된 아이템의 색상
                        else Color(0xFFB0B0B0) // 선택되지 않은 아이템의 색상
                    )
                }
            )
        }
    }
}


fun DrawScope.drawBackgroundShapes() {
    drawCircle(
        color = Color(0xFF9ABDFD),
        center = center.copy(x = center.x - 100.dp.toPx(), y = center.y - 50.dp.toPx()),
        radius = 50.dp.toPx()
    )
    drawCircle(
        color = Color(0xFFB8D4FF),
        center = center.copy(x = center.x + 100.dp.toPx(), y = center.y + 30.dp.toPx()),
        radius = 70.dp.toPx()
    )
    drawCircle(
        color = Color(0xFF7AA8F0),
        center = center.copy(x = center.x - 50.dp.toPx(), y = center.y + 70.dp.toPx()),
        radius = 30.dp.toPx()
    )
}
