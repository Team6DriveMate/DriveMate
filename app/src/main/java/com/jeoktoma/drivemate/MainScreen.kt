package com.jeoktoma.drivemate

import android.net.Uri
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
import androidx.compose.runtime.MutableState
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
import com.google.gson.Gson

//길찾기(path), 운행(drive), 설문(survey), 리마인드(tip), 사용자 프로필(이름, 칭호, 레벨 등 유저 정보)
@Composable
fun MainScreen(navController: NavController, title: String, userName: String, selectedItem: MutableState<Int>) {
    val tips = listOf(
        Triple("주차", "주차가 아직 어렵다면?", R.drawable.tip_main_screen_image_parking),
        Triple("고속도로", "고속도로가 익숙하지 않다면?", R.drawable.tip_main_screen_image_highway),
        Triple("필수 체크", "4개의 필수 체크 항목", R.drawable.tip_main_screen_image_checking),
        Triple("사고 대처", "사고가 났을 때 대처 방법", R.drawable.tip_main_screen_image_accident),
        Triple("도로 매너", "매너 있는 운전자가 되기 위해", R.drawable.tip_main_screen_image_manner)
    )

    // 랜덤한 팁을 선택합니다.
    val randomTip = remember { tips.random() }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController, selectedItem) }
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
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp),
                fontFamily = FontFamily(Font(R.font.freesentation))
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                fontFamily = FontFamily(Font(R.font.freesentation))
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
                            fontSize = 24.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.freesentation)),
                        )
                    )
                    Text(
                        text = randomTip.second,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 18.sp,
                            color = Color.White,
                            fontFamily = FontFamily(Font(R.font.freesentation)),
                        )
                    )
                    Button(
                        onClick = {
                            val tipDetailsMap = mapOf(
                                "주차" to listOf(
                                    TipDetail("평행 주차 (1/4)", "주차된 차량과 약 1m 거리를 두고 진입", R.drawable.parallel_1),
                                    TipDetail("평행 주차 (2/4)", "사이드미러가 앞차의 뒷 범퍼와\n일직선이 되면 정지", R.drawable.parallel_2),
                                    TipDetail("평행 주차 (3/4)", "핸들을 왼쪽으로 들어서 45각도로 살짝\n전진, 앞차와 범퍼가 일직선이 되면 정지", R.drawable.parallel_3),
                                    TipDetail("평행 주차 (4/4)", "핸들을 왼쪽으로 감은 후,\n후진하여 평행주차 완료", R.drawable.parallel_4)
                                ),
                                "고속도로" to listOf(
                                    TipDetail("저속 주행 금지", "고속도로의 기본 속도는 80~100km/h", R.drawable.highway_1),
                                    TipDetail("추월 전용 차로", "고속도로 1차로는 추월차로", R.drawable.highway_2),
                                    TipDetail("터널 통과", "터널 안에서는 차선 변경 불가", R.drawable.highway_3),
                                    TipDetail("차간 거리", "앞 차와 약 100m 정도의 안전거리 유지", R.drawable.highway_4),
                                    TipDetail("주행", "대형 트럭과 나란히 달리지 않기", R.drawable.highway_5)
                                ),
                                "필수 체크" to listOf(
                                    TipDetail("차선", "양쪽 사이드미러로 내 차와 차선 사이의\n간격을 확인하며 거리 맞추기", R.drawable.check_1),
                                    TipDetail("교차로 유도선", "좌회전을 할 때 노면 유도선을\n넘지 않도록 지키기", R.drawable.check_2),
                                    TipDetail("비보호 좌회전", "차량신호가 녹색신호이고 마주 오는\n차량이 없을 때 좌회전 가능", R.drawable.check_3),
                                    TipDetail("방향 지시등", "반드시 방향 지시등을 켜고 차로 변경", R.drawable.check_4)
                                ),
                                "사고 대처" to listOf(
                                    TipDetail("사고 발생", "사고 발생 즉시 인근에 차량을 정차", R.drawable.acc_1),
                                    TipDetail("상해 확인", "차에서 내려 상대방의 상해 정도를 확인\n이때 부상자를 움직이게 하는 행동은\n 부상 부위를 더욱 악화시킬 수 있으므로 삼가", R.drawable.acc_2),
                                    TipDetail("사고 신고", "보험회사와 경찰서에 사고 신고\n가능하면 반드시 보험사를 통해 사고 처리 추천", R.drawable.acc_3),
                                    TipDetail("증거 확보", "원거리 사진, 타이어 및 핸들 사진,\n파손부위 근접 사진,\n상대차량 블랙박스 장착 사진", R.drawable.acc_4),
                                    TipDetail("장소 이동", "상대 차와 함께 안전한 장소로 이동", R.drawable.acc_5)
                                ),
                                "도로 매너" to listOf(
                                    TipDetail("좁은 도로", "되도록 속도나 방향 등 주행 패턴을\n변화하지 않고 천천히 운전", R.drawable.manner_1),
                                    TipDetail("T자 도로", "기본적으로 직진 차량이 우선\n직진 차량이 먼저 지나갈 수 있도록 양보", R.drawable.manner_2),
                                    TipDetail("경사로", "마주 오는 차가 있다면 내려오는 차량에 양보", R.drawable.manner_3),
                                    TipDetail("회전교차로", "우선 진입할 때는 감속 운행을 하고,\n회전하는 차량이 항상 우선이므로\n흐름과 질서에 맞게 양보", R.drawable.manner_4),
                                    TipDetail("비상등 매너", "전방에 사고가 났다는 것을 알려줄 때,\n시야 확보가 어려울 때 전후방 차량에\n내 위치를 알려주기 위해", R.drawable.manner_5)
                                )
                            )
                            val selectedTipDetails = tipDetailsMap[randomTip.first] ?: emptyList()

                            // JSON 직렬화
                            val gson = Gson()
                            val tipDetailsJson = gson.toJson(selectedTipDetails)

                            // Navigation으로 데이터 전달
                            navController.navigate("tipDetailsScreen/${randomTip.first}/${Uri.encode(tipDetailsJson)}")
                                  },
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
                            Text(text = "더보기", color = Color.White, fontFamily = FontFamily(Font(R.font.freesentation)))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // 지난 운행 박스
            Column {
                Text(
                    text = "지난 운행",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 24.sp, fontFamily = FontFamily(Font(R.font.freesentation)))
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
fun BottomNavigationBar(navController: NavController, selectedItem: MutableState<Int>) {
    val items = listOf("Home", "Report", "Path", "Tip", "Profile")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Outlined.Article,
        Icons.Default.Search,
        Icons.Default.Check,
        Icons.Default.Person
    )

    NavigationBar(
        containerColor = Color(0xFFF7F8F8)
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem.value == index,
                onClick = {
                    selectedItem.value = index
                    when (item) {
                        "Home" -> navController.navigate("mainScreen") {
                            launchSingleTop = true
                        }
                        "Report" -> navController.navigate("reportScreen") {
                            launchSingleTop = true
                        }
                        "Path" -> navController.navigate("pathScreen") {
                            launchSingleTop = true
                        }
                        "Tip" -> navController.navigate("tipScreen") {
                            launchSingleTop = true
                        }
                        "Profile" -> navController.navigate("profileScreen") {
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = icons[index],
                        contentDescription = item,
                        tint = if (selectedItem.value == index) Color(0xFF8E44AD) // 선택된 아이템 색상
                        else Color(0xFFB0B0B0) // 선택되지 않은 아이템 색상
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
