package com.jeoktoma.drivemate

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.gson.Gson

@Composable
fun TipScreen(navController: NavController) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController, selectedIndex = 3) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // 상단 제목과 버튼
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = "Tip",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                IconButton(onClick = { /* 점 세 개 버튼 로직 (미정) */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tip 목록
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 각 아이템의 제목, 설명, 이미지 리소스를 리스트로 관리
                val tips = listOf(
                    Triple("주차", "주차가 아직 어렵다면?", R.drawable.tip_main_screen_image_parking),
                    Triple("고속도로", "고속도로가 익숙하지 않다면?", R.drawable.tip_main_screen_image_highway),
                    Triple("필수 체크", "n개의 필수 체크 항목", R.drawable.tip_main_screen_image_checking),
                    Triple("사고 대처", "사고가 났을 때 대처 방법", R.drawable.tip_main_screen_image_accident),
                    Triple("도로 매너", "매너 있는 운전자가 되기 위해", R.drawable.tip_main_screen_image_manner)
                )

                items(tips.size) { index ->
                    TipItem(
                        title = tips[index].first,
                        description = tips[index].second,
                        imageVectorId = tips[index].third, // 각 아이템의 고유 이미지 리소스 ID
                        onMoreClick = {
                            val tipDetails = when (tips[index].first) {
                                "주차" -> listOf(
                                    TipDetail("후면 주차 - 1", "핸들을 끝까지 돌리고 천천히 후진", R.drawable.tip_main_screen_image_accident),
                                    TipDetail("후면 주차 - 2", "45도 각도를 맞추고 후진", R.drawable.tip_main_screen_image_accident),
                                    TipDetail("후면 주차 - 3", "핸들을 원래 방향으로 돌려 후진", R.drawable.tip_main_screen_image_accident)
                                )
                                "고속도로" -> listOf(
                                    TipDetail("고속도로 진입 - 1", "속도를 올리고 진입로로 들어가기", R.drawable.tip_main_screen_image_accident),
                                    TipDetail("고속도로 주행 - 2", "주행 중 차선을 잘 지키기", R.drawable.tip_main_screen_image_accident)
                                )
                                else -> emptyList()
                            }
                            navController.navigate("tipDetailsScreen/${tips[index].first}/${Uri.encode(
                                Gson().toJson(tipDetails))}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TipItem(title: String, description: String, imageVectorId: Int, onMoreClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF92A3FD).copy(alpha = 0.3f), // 투명도 30%
                        Color(0xFF9DCEFF).copy(alpha = 0.3f)  // 투명도 30%
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(2f)
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black
                    )
                )
                Button(
                    onClick = onMoreClick, // 클릭 이벤트 연결
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    )
                ) {
                    Text(text = "더보기", color = Color(0xFF92A3FD))
                }
            }
            Icon(
                painter = painterResource(id = imageVectorId),
                contentDescription = title,
                modifier = Modifier
                    .weight(1f)
                    .size(80.dp)
                    .padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun TipDetailsScreen(navController: NavController, tipTitle: String, tips: List<TipDetail>) {
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
                    text = tipTitle,
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
        bottomBar = { BottomNavigationBar(navController, selectedIndex = 3) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 카드 슬라이드
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(tips) { tipDetail ->
                    TipCard(tipDetail = tipDetail)
                }
            }

            Spacer(modifier = Modifier.height(200.dp))

            // 확인 버튼
            Button(
                onClick = { navController.popBackStack() },
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
                    Text(text = "확인", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TipCard(tipDetail: TipDetail) {
    Box(
        modifier = Modifier
            .width(300.dp)
            .aspectRatio(1.5f)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF92A3FD), Color(0xFF9DCEFF))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 이미지
            Image(
                painter = painterResource(id = tipDetail.imageResId),
                contentDescription = tipDetail.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            // 카드 제목
            Text(
                text = tipDetail.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            // 카드 설명
            Text(
                text = tipDetail.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

data class TipDetail(
    val title: String,
    val description: String,
    val imageResId: Int // 이미지 리소스 ID
)

