package com.jeoktoma.drivemate

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
fun EditProfileScreen(navController: NavController, selectedItem: MutableState<Int>, viewModel: UserViewModel) {
    val selectedTitle = remember { mutableStateOf("") }
    val nickname = remember { mutableStateOf(viewModel.nickname) }
    val isEditingName = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val username = viewModel.username

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
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = "프로필 수정",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 23.sp,
                        fontFamily = FontFamily(Font(R.font.freesentation))
                    )
                )
                IconButton(onClick = { /* 오른쪽 버튼: 아직 미정 */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 사용자 이름과 수정 아이콘
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isEditingName.value) {
                    // 이름 입력 필드
                    androidx.compose.material3.OutlinedTextField(
                        value = nickname.value,
                        onValueChange = { nickname.value = it },
                        label = { Text("닉네임") },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                } else {
                    Column {
                        Text(
                            text = nickname.value,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 27.sp
                            )
                        )
                        Text(
                            text = "${selectedTitle.value.ifEmpty{ "칭호를 선택하세요" }}", // 예시 칭호
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Gray,
                                fontSize = 22.sp,
                                fontFamily = FontFamily(Font(R.font.freesentation))
                            )
                        )
                    }
                }
                IconButton(onClick = { isEditingName.value = !isEditingName.value }) {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Name"
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // 칭호 선택
            Text(
                text = "칭호 선택",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp, fontFamily = FontFamily(Font(R.font.freesentation)))
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val titles = listOf("병아리 운전자", "도로 위 무법자", "평정심", "날씨의 아이", "눈이 두개지요", "사팔뜨기", "준법 시민", "드리블의 귀재", "그대 참치마요")

                items(titles.size) { index ->
                    val title = titles[index]
                    val isOwned = viewModel.titles?.any { it.name == title } ?: false // 칭호 보유 여부 확인
                    if (isOwned) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.2f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    brush = if (selectedTitle.value == title) {
                                        Brush.linearGradient(
                                            colors = listOf(Color(0xFF92A3FD), Color(0xFF9DCEFF))
                                        )
                                    } else {
                                        Brush.linearGradient(
                                            colors = listOf(Color(0xFFF5F5F5), Color(0xFFF5F5F5))
                                        )
                                    }
                                )
                                .clickable { selectedTitle.value = title }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        //fontWeight = FontWeight.Bold,
                                        color = if (selectedTitle.value == title) Color.White else Color.Gray,
                                        fontFamily = FontFamily(Font(R.font.freesentation)),
                                        fontSize = 20.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 수정 완료 버튼
            Button(
                onClick = {
                    val updateRequest = UserUpdateRequest(
                        nickname = nickname.value,
                        mainTitle = selectedTitle.value
                    )
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.updateUserInfo(
                            username = username,
                            userUpdateRequest = updateRequest,
                            context = context,
                            onSuccess = {
                                Log.d(
                                    "EditProfileScreen",
                                    "수정 성공: ${updateRequest.nickname}, ${updateRequest.mainTitle}"
                                )
                                Toast.makeText(context, "수정 완료", Toast.LENGTH_SHORT).show()

//                                viewModel.userInfo = viewModel.userInfo?.let {
//                                    UserInfoResponse(
//                                        nickname = nickname.value,
//                                        username = username,
//                                        mainTitle = selectedTitle.value,
//                                        level = it.level,
//                                        experience = it.experience,
//                                        nextLevelExperience = it.nextLevelExperience,
//                                        weakPoints = it.weakPoints,
//                                        titles = it.titles
//                                    )
//                                }


                                navController.navigate("profileScreen") {
                                    popUpTo("profileScreen") {
                                        inclusive = true
                                    } // 기존 화면 제거 후 다시 생성
                                }
                            },
                            onError = {
                                Log.d(
                                    "EditProfileScreen",
                                    "수정 실패: ${username}, ${updateRequest.nickname}, ${updateRequest.mainTitle}"
                                )
                                Toast.makeText(context, "수정 실패", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), // 버튼의 기본 색을 투명으로 설정
                contentPadding = PaddingValues() // 내용물의 패딩을 없앰
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF92A3FD), // 첫 번째 색 (Hex)
                                    Color(0xFF9DCEFF)  // 두 번째 색 (Hex)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center // 버튼 텍스트를 중앙 정렬
                ) {
                    Text(text = "수정 완료", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.freesentation)))
                }
            }
        }
    }
}
