package com.jeoktoma.drivemate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ProfileScreen(navController: NavController) {
    val userName = remember { mutableStateOf("Gildong Hong") }
    val userTitle = remember { mutableStateOf("코너링이 훌룡하시네요") }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController, 4) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // 상단 버튼과 타이틀
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) { // 이전 화면으로
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew, // 뒤로가기 아이콘 설정
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = "프로필",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp)
                )
                IconButton(onClick = { /* 오른쪽 버튼: 아직 미정 */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert, // 점 세개 아이콘
                        contentDescription = "More"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 사용자 정보
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = userName.value,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = userTitle.value,
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray, fontSize = 20.sp)
                    )
                }
                Button(
                    onClick = { navController.navigate("editProfileScreen") }, // 수정 화면으로 이동
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9DCEFF)
                    )
                ) {
                    Text(text = "수정", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 레벨 바
            Text(
                text = "Level 3",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .background(
                        color = Color(0xFFF0F0F0),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f) // 예시: 70% 차 있음
                        .fillMaxHeight()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF92A3FD), Color(0xFF9DCEFF))
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 나의 취약점
            Text(
                text = "나의 취약점",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        color = Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(16.dp)
                    )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 칭호 목록
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), // 3개의 열로 고정
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp), // 칸 간격
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(6) { // 칭호 칸 6개 예시
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f) // 정사각형 칸
                            .background(
                                color = Color(0xFFF0F0F0),
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                }
            }
        }
    }
}
