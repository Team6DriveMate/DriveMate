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
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.gson.Gson

@Composable
fun TipScreen(navController: NavController, selectedItem: MutableState<Int>) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController, selectedItem) }
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
                        fontSize = 23.sp,
                        fontFamily = FontFamily(Font(R.font.freesentation))
                    )
                )
                ThreeDotMenu(navController = navController) {
                    navController.navigate("loginScreen") {
                        popUpTo("loginScreen") { inclusive = true }
                    }
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
                    Triple("주차", "주차가 아직 어렵다면?", R.drawable.image_parking),
                    Triple("고속도로", "고속도로가 익숙하지 않다면?", R.drawable.image_highway),
                    Triple("필수 체크", "4개의 필수 체크 항목", R.drawable.image_checking),
                    Triple("사고 대처", "사고가 났을 때 대처 방법", R.drawable.image_accident),
                    Triple("도로 매너", "매너 있는 운전자가 되기 위해", R.drawable.image_manner)
                )

                items(tips.size) { index ->
                    TipItem(
                        title = tips[index].first,
                        description = tips[index].second,
                        imageVectorId = tips[index].third, // 각 아이템의 고유 이미지 리소스 ID
                        onMoreClick = {
                            val selectedTip = tips[index]
                            val tipDetails = when (selectedTip.first) {
                                "주차" -> listOf(
                                    TipDetail("평행 주차 (1/4)", "주차된 차량과 약 1m 거리를 두고 진입", R.drawable.parallel_1),
                                    TipDetail("평행 주차 (2/4)", "사이드미러가 앞차의 뒷 범퍼와\n일직선이 되면 정지", R.drawable.parallel_2),
                                    TipDetail("평행 주차 (3/4)", "핸들을 왼쪽으로 들어서 45각도로 살짝\n전진, 앞차와 범퍼가 일직선이 되면 정지", R.drawable.parallel_3),
                                    TipDetail("평행 주차 (4/4)", "핸들을 왼쪽으로 감은 후,\n후진하여 평행주차 완료", R.drawable.parallel_4),

                                    TipDetail("후면 주차 (1/4)", "주차된 차량과 약 1m 거리를 두고\n전진 후, 주차라인 중간에 걸쳐지면 정지", R.drawable.rear_parking_1),
                                    TipDetail("후면 주차 (2/4)", "핸들을 왼쪽으로 감아 전진하고,\n차 모서리가 라인과 일직선이 되면 정지", R.drawable.rear_parking_2),
                                    TipDetail("후면 주차 (3/4)", "핸들을 우측으로 끝까지 감은 후,\n천천히 후진", R.drawable.rear_parking_3),
                                    TipDetail("후면 주차 (4/4)", "차가 평행이 되면 핸들을\n중앙으로 맞춘 후, 후면주차 완료", R.drawable.rear_parking_4),

                                    TipDetail("전면 주차 (1/4)", "주차된 차량과 3m, 최소 차 한대는\n지나갈 넉넉한 공간을 두고 진입", R.drawable.front_parking_1),
                                    TipDetail("전면 주차 (2/4)", "핸들을 왼쪽으로 감아 내 차의 왼쪽\n모서리가 라인에 가까워지도록 전진", R.drawable.front_parking_2),
                                    TipDetail("전면 주차 (3/4)", "핸들을 우측으로 끝까지 감아, 왼쪽 바퀴가\n주차라인에 걸칠 때까지 후진", R.drawable.front_parking_3),
                                    TipDetail("전면 주차 (4/4)", "핸들 왼쪽으로 한바퀴만 감아서\n주차 공간과 나란히 되도록 천천히 전진", R.drawable.front_parking_4)
                                )
                                "고속도로" -> listOf(
                                    TipDetail("저속 주행 금지", "고속도로의 기본 속도는 80~100km/h", R.drawable.highway_1),
                                    TipDetail("추월 전용 차로", "고속도로 1차로는 추월차로", R.drawable.highway_2),
                                    TipDetail("터널 통과", "터널 안에서는 차선 변경 불가", R.drawable.highway_3),
                                    TipDetail("차간 거리", "앞 차와 약 100m 정도의 안전거리 유지", R.drawable.highway_4),
                                    TipDetail("주행", "대형 트럭과 나란히 달리지 않기", R.drawable.highway_5)
                                )
                                "필수 체크" -> listOf(
                                    TipDetail("차선", "양쪽 사이드미러로 내 차와 차선 사이의\n간격을 확인하며 거리 맞추기", R.drawable.check_1),
                                    TipDetail("교차로 유도선", "좌회전을 할 때 노면 유도선을\n넘지 않도록 지키기", R.drawable.check_2),
                                    TipDetail("비보호 좌회전", "차량신호가 녹색신호이고 마주 오는\n차량이 없을 때 좌회전 가능", R.drawable.check_3),
                                    TipDetail("방향 지시등", "반드시 방향 지시등을 켜고 차로 변경", R.drawable.check_4),
                                )
                                "사고 대처" -> listOf(
                                    TipDetail("사고 발생", "사고 발생 즉시 인근에 차량을 정차", R.drawable.acc_1),
                                    TipDetail("상해 확인", "차에서 내려 상대방의 상해 정도를 확인\n이때 부상자를 움직이게 하는 행동은\n 부상 부위를 더욱 악화시킬 수 있으므로 삼가", R.drawable.acc_2),
                                    TipDetail("사고 신고", "보험회사와 경찰서에 사고 신고\n가능하면 반드시 보험사를 통해 사고 처리 추천", R.drawable.acc_3),
                                    TipDetail("증거 확보", "원거리 사진, 타이어 및 핸들 사진,\n파손부위 근접 사진,\n상대차량 블랙박스 장착 사진", R.drawable.acc_4),
                                    TipDetail("장소 이동", "상대 차와 함께 안전한 장소로 이동", R.drawable.acc_5),
                                )
                                "도로 매너" -> listOf(
                                    TipDetail("좁은 도로", "되도록 속도나 방향 등 주행 패턴을\n변화하지 않고 천천히 운전", R.drawable.manner_1),
                                    TipDetail("T자 도로", "기본적으로 직진 차량이 우선\n직진 차량이 먼저 지나갈 수 있도록 양보", R.drawable.manner_2),
                                    TipDetail("경사로", "마주 오는 차가 있다면 내려오는 차량에 양보", R.drawable.manner_3),
                                    TipDetail("회전교차로", "우선 진입할 때는 감속 운행을 하고,\n회전하는 차량이 항상 우선이므로\n흐름과 질서에 맞게 양보", R.drawable.manner_4),
                                    TipDetail("비상등 매너", "전방에 사고가 났다는 것을 알려줄 때,\n시야 확보가 어려울 때 전후방 차량에\n내 위치를 알려주기 위해", R.drawable.manner_5),

                                )
                                else -> emptyList()
                            }
                            val gson = Gson()
                            val tipDetailsJson = gson.toJson(tipDetails)

                            // Navigation으로 데이터 전달
                            navController.navigate("tipDetailsScreen/${selectedTip.first}/${Uri.encode(tipDetailsJson)}")
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
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(R.font.freesentation))
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(R.font.freesentation))
                    )
                )
                Button(
                    onClick = onMoreClick, // 클릭 이벤트 연결
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    )
                ) {
                    Text(text = "더보기", color = Color(0xFF92A3FD), fontFamily = FontFamily(Font(R.font.freesentation)))
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
fun TipDetailsScreen(navController: NavController, tipTitle: String, tips: List<TipDetail>, selectedItem: MutableState<Int>) {
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
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.freesentation)))
                )
                IconButton(onClick = { /* 점 세 개 버튼 로직 (미정) */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More"
                    )
                }
            }
        },
        bottomBar = { BottomNavigationBar(navController, selectedItem) }
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

            Spacer(modifier = Modifier.height(160.dp))

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
                    Text(text = "확인", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily(Font(R.font.freesentation)))
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
            .aspectRatio(0.7f)
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
            Spacer(modifier = Modifier.height(16.dp))
            // 이미지
            Image(
                painter = painterResource(id = tipDetail.imageResId),
                contentDescription = tipDetail.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            // 카드 제목
            Text(
                text = tipDetail.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.freesentation))
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            // 카드 설명
            Text(
                text = tipDetail.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.freesentation)),
                    letterSpacing = 0.5f.sp
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


// 테스트 데이터
val sampleTipDetail = TipDetail(
    "평행 주차",
    "이때 부상자를 움직이게 하는 행동은\n부상 부위를 더욱 악화시킬 수 있으므로 삼가",
    imageResId = R.drawable.parallel_4 // 여기에 샘플 이미지 리소스를 사용
)

// TipCard 프리뷰
@Preview(showBackground = true)
@Composable
fun PreviewTipCard() {
    TipCard(tipDetail = sampleTipDetail)
}