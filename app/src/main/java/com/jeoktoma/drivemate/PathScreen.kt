package com.jeoktoma.drivemate

import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.skt.tmap.TMapGpsManager
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import com.skt.tmap.overlay.TMapMarkerItem

@Composable
fun PathScreen(navController: NavController, selectedItem: MutableState<Int>) {
    LocationUtils()

    val context = LocalContext.current

    // 출발지와 목적지 입력 상태 관리
    var startPoint by remember { mutableStateOf(TextFieldValue("")) }
    var endPoint by remember { mutableStateOf(TextFieldValue("")) }

    // 지도 초기화 및 현재 위치 추적 상태 관리
    val tMapView = remember { TMapView(context).apply { setSKTMapApiKey("pANn2nD9NfahYdvrZFaQV2oLpMov5xnV5GMfNIih") } }
    var currentLocation by remember { mutableStateOf<Location?>(null) }

    // 마커 추가
    val markerItem = TMapMarkerItem()



    // GPS 매니저 초기화 및 현재 위치 업데이트 설정 (Compose에서는 Activity가 없으므로 Context로 처리)
    DisposableEffect(Unit) {
        val tMapGps = TMapGpsManager(context).apply {
            minTime = 1000L // 1초마다 업데이트
            minDistance = 5f // 5미터마다 업데이트
            provider = TMapGpsManager.PROVIDER_GPS // GPS 사용 설정

            setOnLocationChangeListener { location ->
                currentLocation = location // 현재 위치 업데이트
                tMapView.setLocationPoint(location.longitude, location.latitude)
                tMapView.setCenterPoint(location.longitude, location.latitude)

                // 현재 위치 좌표 설정 (위도/경도)
                val tMapPoint = TMapPoint(location.longitude, location.latitude)
            }
        }

        tMapGps.openGps()

        onDispose {
            tMapGps.closeGps()
        }
    }



    // UI 구성: 지도 + 출발지/목적지 입력 필드 + 버튼들
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        AndroidView(
            factory = { tMapView },
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = startPoint,
            onValueChange = { startPoint = it },
            label = { Text("출발지 입력") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = endPoint,
            onValueChange = { endPoint = it },
            label = { Text("목적지 입력") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // 길찾기 로직 추가 예정 (출발지와 목적지를 이용한 경로 탐색)
            Toast.makeText(context, "길찾기 기능은 아직 구현되지 않았습니다.", Toast.LENGTH_SHORT).show()
        }) {
            Text("길찾기")
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
