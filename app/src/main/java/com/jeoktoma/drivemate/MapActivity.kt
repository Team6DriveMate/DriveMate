package com.jeoktoma.drivemate

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jeoktoma.drivemate.R
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

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val fm = supportFragmentManager
        var mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance()
            fm.beginTransaction().add(R.id.map_fragment, mapFragment).commit()
        }

        mapFragment?.getMapAsync(this)


    }

    override fun onMapReady(naverMap: NaverMap) {
        //도로 혼잡도 표시
        //naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRAFFIC, true)

        naverMap.locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationTrackingMode = LocationTrackingMode.NoFollow

        val start_lat = intent.getDoubleExtra("start_lat", 0.0)
        val start_lng = intent.getDoubleExtra("start_lng", 0.0)
        val end_lat = intent.getDoubleExtra("latitude", 0.0)
        val end_lng = intent.getDoubleExtra("longitude", 0.0)

        CoroutineScope(Dispatchers.Main).launch {
            val routeResponse = setPathService(start_lat, start_lng, end_lat, end_lng, baseContext)

            if (routeResponse != null){
                val multipartPath = MultipartPathOverlay()
                val coordParts = mutableListOf<MutableList<LatLng>>()
                val colorParts = mutableListOf<MultipartPathOverlay.ColorPart>()

                routeResponse.route.segments.forEachIndexed { index, segment ->
                    // 각 세그먼트의 좌표를 리스트에 추가
                    coordParts.add(segment.path.map { LatLng(it.lat, it.lng) }.toMutableList())
                    // 각 세그먼트의 색상을 리스트에 추가
                    colorParts.add(MultipartPathOverlay.ColorPart(
                        getColorForTraffic(segment.traffic), Color.BLACK, Color.GRAY, Color.LTGRAY))


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
                naverMap.moveCamera(CameraUpdate.fitBounds(multipartPath.bounds, 100))

                // 총 거리와 시간 표시

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