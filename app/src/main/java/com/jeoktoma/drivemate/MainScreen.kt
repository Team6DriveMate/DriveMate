package com.jeoktoma.drivemate

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.PathOverlay
import com.naver.maps.map.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

//길찾기(path), 운행(drive), 설문(survey), 리마인드(tip), 사용자 프로필(이름, 칭호, 레벨 등 유저 정보)
@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun MainScreen(navController: NavController, viewModel: UserViewModel, selectedItem: MutableState<Int>) {
    val tips = listOf(
        Triple("주차", "주차가 아직 어렵다면?", R.drawable.image_parking),
        Triple("고속도로", "고속도로가 익숙하지 않다면?", R.drawable.image_highway),
        Triple("필수 체크", "4개의 필수 체크 항목", R.drawable.image_checking),
        Triple("사고 대처", "사고가 났을 때 대처 방법", R.drawable.image_accident),
        Triple("도로 매너", "매너 있는 운전자가 되기 위해", R.drawable.image_manner)
    )

    // 랜덤한 팁을 선택합니다.
    val randomTip = remember { tips.random() }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // API 호출 결과 상태 관리
    var reports by remember { mutableStateOf<List<Report>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var detailReport by remember { mutableStateOf<DetailReportResponse?>(null) }

    // API 호출
    LaunchedEffect(key1 = Unit) {
        scope.launch {
            val response = getReportList(context)
            if (response != null) {
                reports = response.reports
            }
            isLoading = false
        }
    }

    val latestReport = reports.maxByOrNull { it.reportId }

    if(latestReport != null) {
        LaunchedEffect(key1 = latestReport!!.reportId) {
            isLoading = true
            detailReport = getReportDetail(latestReport.reportId, context)
            isLoading = false
        }
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
                Text(
                    text = "Drive Mate",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.ydestreet))
                )
                ThreeDotMenu(navController = navController) {
                    navController.navigate("loginScreen") {
                        popUpTo("loginScreen") { inclusive = true }
                    }
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
                .padding(bottom = 16.dp)
        ) {
            // DriveMate 타이틀
//            Text(
//                text = "DriveMate",
//                fontSize = 32.sp,
//                fontFamily = FontFamily(Font(R.font.ydestreet)), // 커스텀 폰트 사용
//                modifier = Modifier.padding(vertical = 32.dp)
//            )
            Row (modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start) {

                Spacer(modifier = Modifier.width(10.dp))

                // 원형 레벨바
                Box(modifier = Modifier.size(60.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(100.dp)) {
                        drawCircle(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Gray, // 첫 번째 색 (Hex)
                                    Color.LightGray  // 두 번째 색 (Hex)
                                )
                            ),
                            style = Stroke(width = 12.dp.toPx())
                        )
                        drawArc(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF92A3FD), // 첫 번째 색 (Hex)
                                    Color(0xFF9DCEFF)  // 두 번째 색 (Hex)
                                )
                            ),
                            startAngle = -90f,
                            sweepAngle = viewModel.experience * 3.6f, // 예시로 120도 만큼 채우기
                            useCenter = false,
                            style = Stroke(width = 12.dp.toPx())
                        )
                    }
                    Text(
                        text = "Lv.${viewModel.level}", style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp),
                        fontFamily = FontFamily(Font(R.font.freesentation))
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                Column() {
                    // 사용자 칭호와 이름
                    Text(
                        text = viewModel.nickname,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        ),
                        fontFamily = FontFamily(Font(R.font.freesentation))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = viewModel.title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp),
                        fontFamily = FontFamily(Font(R.font.freesentation))
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            // 오늘의 꿀팁 카드
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .shadow(
                        elevation = 8.dp, // 그림자의 높이 설정
                        shape = MaterialTheme.shapes.medium, // 그림자의 모양 설정 (예: 둥근 모서리)
                        ambientColor = Color.Gray, // 환경광 그림자 색상
                        spotColor = Color.Black // 스팟 그림자 색상
                    )
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

            Spacer(modifier = Modifier.height(12.dp))

            // 지도 및 출발지/목적지 표시 영역
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(
                        elevation = 8.dp, // 그림자의 높이 설정
                        shape = MaterialTheme.shapes.medium, // 그림자의 모양 설정 (예: 둥근 모서리)
                        ambientColor = Color.Gray, // 환경광 그림자 색상
                        spotColor = Color.Black // 스팟 그림자 색상
                    )
                    .background(Color(0xFFF7F8F8), shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = Color(0xFFF9F9F9), shape = RoundedCornerShape(8.dp))
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "저번 운행",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                fontFamily = FontFamily(Font(R.font.freesentation)),
                                fontSize = 20.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${latestReport?.title ?: ""}",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                                fontFamily = FontFamily(Font(R.font.freesentation)),
                                fontSize = 16.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            if (isLoading) {
                                // 로딩 중 표시
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("로딩 중...")
                                }
                            } else {
                                detailReport?.let { report ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(androidx.compose.ui.graphics.Color.LightGray) // 지도 placeholder (네이버 지도 SDK로 대체 가능)
                                    ) {
                                        val path = report?.path
                                        val cameraPositionState: CameraPositionState =
                                            rememberCameraPositionState()

                                        var mapUiSettings by remember {
                                            mutableStateOf(
                                                MapUiSettings(
                                                    isScrollGesturesEnabled = false,
                                                    isZoomGesturesEnabled = false,
                                                    isTiltGesturesEnabled = false,
                                                    isRotateGesturesEnabled = false,
                                                    isLocationButtonEnabled = false,
                                                    isCompassEnabled = false,
                                                    isScaleBarEnabled = false,
                                                    isZoomControlEnabled = false
                                                )
                                            )
                                        }

                                        NaverMap(modifier = Modifier.fillMaxSize(),
                                            cameraPositionState = cameraPositionState,
                                            uiSettings = mapUiSettings,
                                            onMapLoaded = {
                                                val coordinates =
                                                    path!!.route.segments.flatMap { segment ->
                                                        segment.path.map { LatLng(it.lat, it.lng) }
                                                    }
                                                val bounds = CameraUpdate.fitBounds(
                                                    LatLngBounds.Builder().include(coordinates)
                                                        .build(),
                                                    100
                                                )

                                                cameraPositionState.move(bounds)
                                            }
                                        ) {
                                            path?.route?.segments?.forEach { segment ->
                                                PathOverlay(
                                                    coords = segment.path.map {
                                                        LatLng(
                                                            it.lat,
                                                            it.lng
                                                        )
                                                    },
                                                    color = when (segment.traffic) {
                                                        "1" -> Color.Green  // 원활
                                                        "2" -> Color.Yellow // 서행
                                                        "3" -> Color(0xFFFFA500)// 지체
                                                        "4" -> Color.Red   // 정체
                                                        else -> Color.Gray // 알 수 없음
                                                    },
                                                    width = 10.dp, // 경로의 두께 설정
                                                    outlineColor = Color.Black,
                                                    outlineWidth = 2.dp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
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

@Composable
fun ThreeDotMenu(navController: NavController, onLogout: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { showMenu = !showMenu }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More",
                tint = Color.Black
            )
        }
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) { Text(
                    text = "로그아웃",
                    fontFamily = FontFamily(Font(R.font.freesentation)),
                    fontSize = 18.sp
                    ) }
                },
                onClick = {
                    showMenu = false
                    onLogout()
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
