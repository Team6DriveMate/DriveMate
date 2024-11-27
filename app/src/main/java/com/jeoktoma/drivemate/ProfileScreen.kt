package com.jeoktoma.drivemate

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
fun ProfileScreen(navController: NavController, selectedItem: MutableState<Int>, viewModel: UserViewModel) {
    val nickname = remember { mutableStateOf("Gildong Hong") }
    val userTitle = remember { mutableStateOf("코너링이 훌룡하시네요") }
    val userInfo = remember { mutableStateOf<UserInfoResponse?>(null) }
    val username = viewModel.userInfo?.username?: ""
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Log.d("ProfileScreen", "getUserInfo 호출 시작: $username")
        viewModel.getUserInfo(username, context) { response ->
            if (response == null) {
                Log.d("ProfileScreen", "갱신된 데이터: $response")
                Log.e("ProfileScreen", "API 응답이 null입니다.")
            } else {
                Log.d("ProfileScreen", "API 응답: $response")
            }
            userInfo.value = response
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController, selectedItem) }
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
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 23.sp
                    )
                )
                IconButton(onClick = { /* 오른쪽 버튼: 아직 미정 */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert, // 점 세개 아이콘
                        contentDescription = "More"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            userInfo.value?.let { user ->
                // 사용자 정보
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = user.nickname,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 27.sp,
                                fontFamily = FontFamily(Font(R.font.freesentation))
                            )
                        )
                        Text(
                            text = user.mainTitle,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Gray,
                                fontSize = 22.sp,
                                fontFamily = FontFamily(Font(R.font.freesentation))
                            )
                        )
                    }
                    Button(
                        onClick = { navController.navigate("editProfileScreen") }, // 수정 화면으로 이동
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9DCEFF)
                        )
                    ) {
                        Text(
                            text = "수정",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.freesentation))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 레벨 바
                Text(
                    text = "Level ${user.level}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 23.sp,
                        fontFamily = FontFamily(Font(R.font.freesentation))
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

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

                Spacer(modifier = Modifier.height(36.dp))

                // 나의 취약점
                Text(
                    text = "나의 취약점",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.freesentation))
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    if(user.weakPoints != null) {
                        user.weakPoints.forEach { weakPoint ->
                            Text(
                                text = weakPoint,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.Gray,
                                    fontSize = 20.sp,
                                    fontFamily = FontFamily(Font(R.font.freesentation))
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 칭호 목록
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3), // 3개의 열로 고정
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp), // 칸 간격
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(user.titles.size) { index ->
                        val title = user.titles[index] // 칭호 칸 6개 예시
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f) // 정사각형 칸
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFFF5F5F5), Color(0xFFF5F5F5))
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = title.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.Gray,
                                        fontSize = 20.sp,
                                        fontFamily = FontFamily(Font(R.font.freesentation))
                                    )
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun ProfileScreenPreview() {
//    val navController = rememberNavController()
//    val selectedItem = remember { mutableStateOf(0) }
//    val viewModel = UserViewModel()
//    val username = "testUser"
//
//    DriveMateTheme {
//        ProfileScreen(
//            navController = navController,
//            selectedItem = selectedItem,
//            viewModel = viewModel,
//            username = username
//        )
//    }
//}