package com.jeoktoma.drivemate

import android.content.Intent
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.unit.dp
import com.jeoktoma.drivemate.R
import com.kakaomobility.knsdk.KNLanguageType
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Align
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.MultipartPathOverlay
import com.naver.maps.map.overlay.MultipartPathOverlay.ColorPart
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var totalDistanceView: TextView
    private lateinit var estimatedTimeView: TextView
    private lateinit var estimatedArrivalView: TextView
    private lateinit var startNavigationButton: Button

    private val layover = mutableListOf<Point>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)


        totalDistanceView = findViewById(R.id.total_distance)
        estimatedTimeView = findViewById(R.id.estimated_time)
        estimatedArrivalView = findViewById(R.id.estimated_arrival)
        startNavigationButton = findViewById(R.id.start_navigation)

        // 초기에는 모든 View를 숨김 (INVISIBLE 또는 GONE 사용 가능)
        totalDistanceView.visibility = View.GONE
        estimatedTimeView.visibility = View.GONE
        estimatedArrivalView.visibility = View.GONE
        startNavigationButton.visibility = View.INVISIBLE

        val fm = supportFragmentManager
        var mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance()
            fm.beginTransaction().add(R.id.map_fragment, mapFragment).commit()
        }

        mapFragment?.getMapAsync(this)


        startNavigationButton.setOnClickListener {
            // 길 안내 화면으로 전환
            val intent = Intent(this, KakaoActivity::class.java)

            val latList = ArrayList<Double>()
            val lngList = ArrayList<Double>()

            layover.forEach { part ->
                latList.add(part.lat)
                lngList.add(part.lng)
            }
            intent.putExtra("lat_list", latList)
            intent.putExtra("lng_list", lngList)
             // 필요한 경우 경로 정보를 intent에 추가
            /**
             * 길찾기 SDK 인증을 진행합니다.
             */
            KakaoNavi.knsdk.apply {
                initializeWithAppKey(
                    aAppKey = "704f94e0ccd82aa85319a646ada05bcf",       // 카카오디벨로퍼스에서 부여 받은 앱 키
                    aClientVersion = "1.9.4",                                               // 현재 앱의 클라이언트 버전
                    aUserKey = "1157097",                                                  // 사용자 id
                    aLangType = KNLanguageType.KNLanguageType_KOREAN,   // 언어 타입
                    aCompletion = {

                        // Toast는 UI를 갱신하는 작업이기 때문에 UIThread에서 동작되도록 해야 합니다.
                        runOnUiThread {
                            if (it != null) {
                                Toast.makeText(applicationContext, "인증에 실패하였습니다", Toast.LENGTH_LONG).show()

                            } else {
                                Toast.makeText(applicationContext, "인증 성공하였습니다", Toast.LENGTH_LONG).show()

                                this@MapActivity.startActivity(intent)
                            }
                        }
                    })
            }
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        //도로 혼잡도 표시
        //naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRAFFIC, true)

        naverMap.locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        // 아래 정보창을 위한 패딩
        naverMap.setContentPadding(0,0,0,200)

        val start_lat = intent.getDoubleExtra("start_lat", 0.0)
        val start_lng = intent.getDoubleExtra("start_lng", 0.0)
        val end_lat = intent.getDoubleExtra("latitude", 0.0)
        val end_lng = intent.getDoubleExtra("longitude", 0.0)

        CoroutineScope(Dispatchers.Main).launch {
            val routeResponse = setPathService(start_lat, start_lng, end_lat, end_lng, baseContext)

            if (routeResponse != null){
                val multipartPath = MultipartPathOverlay()
                val colorParts = mutableListOf<MultipartPathOverlay.ColorPart>()
                val coordParts = mutableListOf<MutableList<LatLng>>()

                routeResponse.route.segments.forEachIndexed { index, segment ->
                    // 각 세그먼트의 좌표를 리스트에 추가
                    coordParts.add(segment.path.map { LatLng(it.lat, it.lng) }.toMutableList())
                    // 각 세그먼트의 색상을 리스트에 추가
                    colorParts.add(MultipartPathOverlay.ColorPart(
                        getColorForTraffic(segment.traffic), Color.BLACK, Color.GRAY, Color.LTGRAY))

                    layover.add(segment.startPoint)


                    // 끝점 마커 (마지막 세그먼트만)
                    if (index == routeResponse.route.segments.size - 1) {
                        val endmarker = Marker().apply {
                            position = LatLng(end_lat, end_lng)
                            icon = MarkerIcons.BLACK
                            iconTintColor = Color.RED
                            captionText = "도착"
                            captionTextSize = 16f
                            setCaptionAligns(Align.Top)
                            isHideCollidedSymbols = true
                            map = naverMap
                        }

                        layover.add(segment.endPoint)
                    }
                }

                // MultipartPathOverlay 설정
                multipartPath.coordParts = coordParts
                multipartPath.colorParts = colorParts
                multipartPath.width = 30
                multipartPath.patternImage = OverlayImage.fromResource(R.drawable.path_pattern12)
                multipartPath.patternInterval = 60
                multipartPath.map = naverMap

                // 카메라 이동
                naverMap.moveCamera(CameraUpdate.fitBounds(multipartPath.bounds, 300))

                // 총 거리와 시간 표시
                updateRouteInfo(routeResponse)

                // 레이아웃 표시
                totalDistanceView.visibility = View.VISIBLE
                estimatedTimeView.visibility = View.VISIBLE
                estimatedArrivalView.visibility = View.VISIBLE
                startNavigationButton.visibility = View.VISIBLE
            }
            else{   // 만약 response가 null이라면 전으로 돌아감
                val intent = Intent(this@MapActivity, NavActivity::class.java)

                startActivity(intent)
                finish()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }


    private fun updateRouteInfo(routeResponse: RouteResponse) {
        val totalDistance = routeResponse.totalDistance
        val totalTime = routeResponse.totalTime

        totalDistanceView.text = "${formatDistance(totalDistance)}"
        estimatedTimeView.text = "${formatTime(totalTime)}"

        // 예상 도착 시간 계산
        val arrivalTime = Calendar.getInstance().apply {
            add(Calendar.SECOND, totalTime)
        }
        val arrivalTimeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(arrivalTime.time)
        estimatedArrivalView.text = "$arrivalTimeString 도착"
    }

    private fun formatDistance(meters: Int): String {
        return if (meters >= 1000) {
            String.format("%.1f km", meters / 1000.0)
        } else {
            "$meters m"
        }
    }

    private fun formatTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        return when {
            hours > 0 -> String.format("%d시간 %d분", hours, minutes)
            else -> String.format("%d분", minutes)
        }
    }

}

// 교통 상황에 따른 색상 지정
fun getColorForTraffic(traffic: String): Int {
    return when (traffic) {
        "1" -> Color.GREEN  // 원활
        "2" -> Color.YELLOW // 서행
        "3" -> Color.HSVToColor(floatArrayOf(36f, 100f, 100f))// 지체
        "4" -> Color.RED    // 정체
        else -> Color.GRAY  // 알 수 없음
    }
}

