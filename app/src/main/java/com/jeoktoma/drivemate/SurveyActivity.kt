package com.jeoktoma.drivemate

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.CircleOverlay
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.max
import androidx.compose.ui.graphics.Color as uicolor

class SurveyActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap

    private val markers = mutableListOf<Marker>()
    private val pathOverlays = mutableListOf<PathOverlay>()
    private val infoWindows = mutableListOf<InfoWindow>()
    private val circleOverlays = mutableListOf<CircleOverlay>()

    private var currentState: MapState = MapState.FULL_ROUTE
    private var currentSection: GetSection? = null
    private var currentSegment: GetSegment? = null

    private var currentSectionIndex = -1
    private var currentSegmentIndex = -1

    lateinit var getRouteResponse: GetRouteResponse

    private val showBack = mutableStateOf(true)
    private val showNext = mutableStateOf(true)

    val pathOutlineWidth = 5

    enum class MapState {
        FULL_ROUTE,
        SECTION,
        SEGMENT
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)

        mapView = findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // TopBar를 Compose로 추가
        val composeContainer = findViewById<FrameLayout>(R.id.compose_container_topbar)
        composeContainer.addView(ComposeView(this).apply {
            setContent {
                TopBar(
                    onBackPressed = {
                        when (currentState) {
                            MapState.SECTION -> showFullRoute(getRouteResponse)
                            MapState.SEGMENT -> {
                                showSection(currentSection!!)
                            }

                            else -> Unit
                        }
                        // 이전 화면으로 돌아가기
                    },
                    onNextPressed = {
                        when (currentState) {
                            MapState.FULL_ROUTE -> currentSection?.let { showSection(it) }
                            MapState.SECTION -> currentSegment?.let { showSegment(it) }
                            else -> Unit
                        }
                    }, showBack.value, showNext.value
                )
            }
        })


    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        naverMap.lightness = 0.5f
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRAFFIC, false)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, false)

//        val uiSettings = naverMap.uiSettings
//        uiSettings.setAllGesturesEnabled(false)
//        val options = NaverMapOptions().zoomControlEnabled(false)

        CoroutineScope(Dispatchers.Main).launch {
            val getRouteResponse = performGetRouteService(baseContext)

            if (getRouteResponse != null) {
                showFullRoute(getRouteResponse)


                // PASS 버튼 추가
                val btnPass = findViewById<Button>(R.id.btn_pass)
                btnPass.visibility = View.VISIBLE
                btnPass.setOnClickListener {
                    val intent = Intent(this@SurveyActivity, OverallSurveyActivity::class.java)
                    startActivity(intent)
                }


                // 초기 상태에서 버튼 보이기
                btnPass.visibility = View.VISIBLE

                showFullRoute(getRouteResponse)
            }
        }
    }


    internal fun showFullRoute(getRouteResponse: GetRouteResponse) {
        currentState = MapState.FULL_ROUTE
        showBack.value = false
        showNext.value = false
        clearMap()

        getRouteResponse.route.sections.forEach { section ->
            section.segments.forEach { segment ->
                val path = PathOverlay()
                path.coords = segment.path.map { LatLng(it.lat, it.lng) }
                path.color = getColorForTraffic(segment.traffic)
                path.width = 15 // 경로의 두께 설정
                path.map = naverMap
                path.outlineWidth = 0
                path.tag = section.sectionIndex!!.toInt()
                pathOverlays.add(path)
            }
        }
        val coordinates = getRouteResponse.route.sections.flatMap { section ->
            section.segments.flatMap { segment ->
                segment.path.map { LatLng(it.lat, it.lng) }
            }
        }


        addSectionMarkers(getRouteResponse)

        val bounds = LatLngBounds.Builder().include(coordinates).build()
        naverMap.moveCamera(CameraUpdate.fitBounds(bounds, 200))
    }

    private fun addSectionMarkers(getRouteResponse: GetRouteResponse) {
        getRouteResponse.route.sections.forEach { section ->
            // 모든 segment의 path를 하나의 리스트로 합칩니다.
            val allPathPoints = section.segments.flatMap { it.path }

            // 전체 경로의 중간 지점을 찾습니다.
            val avgPoint = Point(
                (allPathPoints.first().lat + allPathPoints.last().lat) / 2,
                (allPathPoints.first().lng + allPathPoints.last().lng) / 2
            )

            val midPoint = allPathPoints.minByOrNull { point ->
                val latDiff = point.lat - avgPoint.lat
                val lngDiff = point.lng - avgPoint.lng
                latDiff * latDiff + lngDiff * lngDiff
            } ?: avgPoint

            val infoWindow = InfoWindow()
            infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(this) {
                override fun getText(infoWindow: InfoWindow): CharSequence {
                    return "${section.sectionName}"
                }
            }
            infoWindow.position = LatLng(midPoint.lat, midPoint.lng)
            infoWindow.zIndex = 10000 // InfoWindow를 다른 오버레이보다 위에 표시
            infoWindow.alpha = 0.7f
            infoWindow.map = naverMap
            infoWindow.tag = section.sectionIndex!!.toInt()
            infoWindows.add(infoWindow)

            infoWindow.setOnClickListener {
                currentSection = section
                toggleSectionHighlight(section.sectionIndex!!.toInt())
                true
            }

//            addCircleOverlay(section.segments.first().startPoint, Color.RED, "S")
//            addCircleOverlay(section.segments.last().endPoint, Color.RED, "E")
        }
    }

    private fun toggleSectionHighlight(index: Int) {
        if (currentSectionIndex == index) {
            // 이미 선택된 섹션을 다시 클릭하면 하이라이트 제거
            pathOverlays.filter { it.tag == index }.forEach {
                it.outlineWidth = 0
            }
            infoWindows.find { it.tag == index }?.alpha = 0.7f
            currentSectionIndex = -1
            showNext.value = false
        } else {
            // 새로운 섹션 선택
            pathOverlays.forEach { it.outlineWidth = 0 } // 모든 섹션의 하이라이트 제거
            pathOverlays.filter { it.tag == index }.forEach {
                it.outlineWidth = pathOutlineWidth // 선택된 섹션에 테두리 추가
            }
            infoWindows.forEach { it.alpha = 0.7f }
            infoWindows.find { it.tag == index }?.alpha = 1f
            currentSectionIndex = index
            showNext.value = true
            // add
        }
    }

    private fun showSection(section: GetSection) {
        currentState = MapState.SECTION
        currentSection = section
        showBack.value = true
        showNext.value = false
        clearMap()

        val sectionCoordinates = section.segments.flatMap { it.path }
        section.segments.forEach { segment ->
            val path = PathOverlay()
            path.coords = segment.path.map { LatLng(it.lat, it.lng) }
            path.color = getColorForTraffic(segment.traffic)
            path.width = 15 // 경로의 두께 설정
            path.outlineWidth = 0
            path.tag = segment.segmentIndex!!.toInt()
            path.map = naverMap
            pathOverlays.add(path)
        }

        val bounds =
            LatLngBounds.Builder().include(sectionCoordinates.map { LatLng(it.lat, it.lng) })
                .build()
        naverMap.moveCamera(CameraUpdate.fitBounds(bounds, 200))

        addSegmentMarkers(section)
    }

    private fun addSegmentMarkers(section: GetSection) {
        section.segments.forEach { segment ->
            // 전체 경로의 중간 지점을 찾습니다.
            val avgPoint = Point(
                (segment.path.first().lat + segment.path.last().lat) / 2,
                (segment.path.first().lng + segment.path.last().lng) / 2
            )

            val midPoint = segment.path.minByOrNull { point ->
                val latDiff = point.lat - avgPoint.lat
                val lngDiff = point.lng - avgPoint.lng
                latDiff * latDiff + lngDiff * lngDiff
            } ?: avgPoint

            val infoWindow = InfoWindow()
            infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(this) {
                override fun getText(infoWindow: InfoWindow): CharSequence {
                    return "${segment.roadName}"
                }
            }
            infoWindow.position = LatLng(midPoint.lat, midPoint.lng)
            infoWindow.zIndex = 10000 // InfoWindow를 다른 오버레이보다 위에 표시
            infoWindow.alpha = 0.7f
            infoWindow.map = naverMap
            infoWindow.tag = segment.segmentIndex!!.toInt()
            infoWindows.add(infoWindow)

//            addCircleOverlay(segment.startPoint, Color.RED, "S")
//            addCircleOverlay(segment.endPoint, Color.RED, "E")

            infoWindow.setOnClickListener {
                currentSegment = segment
                toggleSegmentHighlight(segment.segmentIndex!!.toInt())
                true
            }
        }
    }

    private fun toggleSegmentHighlight(index: Int) {
        if (currentSegmentIndex == index) {
            // 이미 선택된 섹션을 다시 클릭하면 하이라이트 제거
            pathOverlays.filter { it.tag == index }.forEach {
                it.outlineWidth = 0
            }
            infoWindows.find { it.tag == index }?.alpha = 0.7f
            currentSegmentIndex = -1
            showNext.value = false
        } else {
            // 새로운 섹션 선택
            pathOverlays.forEach { it.outlineWidth = 0 } // 모든 섹션의 하이라이트 제거
            pathOverlays.filter { it.tag == index }.forEach {
                it.outlineWidth = pathOutlineWidth // 선택된 섹션에 테두리 추가
            }
            infoWindows.forEach { it.alpha = 0.7f }
            infoWindows.find { it.tag == index }?.alpha = 1f
            currentSegmentIndex = index
            showNext.value = true
        }
    }

    private fun showSegment(segment: GetSegment) {
        currentState = MapState.SEGMENT
        currentSegment = segment
        showBack.value = true
        showNext.value = false
        clearMap()

        val path = PathOverlay()
        path.coords = segment.path.map { LatLng(it.lat, it.lng) }
        path.color = getColorForTraffic(segment.traffic)
        path.width = 10 // 경로의 두께 설정
        path.outlineWidth = pathOutlineWidth
        path.map = naverMap
        pathOverlays.add(path)

        val bounds =
            LatLngBounds.Builder().include(segment.path.map { LatLng(it.lat, it.lng) }).build()
        naverMap.moveCamera(CameraUpdate.fitBounds(bounds, 100))

//        addCircleOverlay(segment.startPoint, Color.RED, "S")
//        addCircleOverlay(segment.endPoint, Color.BLUE, "E")
        // TODO: 여기에 Segment 상세 정보를 표시하는 로직 추가
        when (currentState) {
            MapState.SEGMENT -> {
                // ComposeView로 SegmentSurveyScreen 렌더링
                val composeContainer = findViewById<FrameLayout>(R.id.compose_container)
                composeContainer.visibility = View.VISIBLE

                // 기존 View를 삭제하고 새로운 ComposeView 렌더링
                composeContainer.removeAllViews()

                val composeView = ComposeView(this)
                composeView.setContent {
                    SegmentSurveyScreen(
                        roadName = segment.roadName ?: "Unnamed Road",
                        segmentIndex = segment.segmentIndex!!.toInt() ?: 0,
                        totalSegments = 3, // 총 세그먼트 수를 전달합니다.
                        surveyViewModel = SurveyViewModel(),
                        segmentCoords = path.coords, // 지도 경로 전달
                        context = this,
                        traffic = when(segment.traffic) {
                            "1" -> "원활"
                            "2" -> "서행"
                            "3" -> "지체"
                            "4" -> "정체"
                            else -> "혼잡도 정보 없음"
                        },
                        roadType = segment.roadType,
                        onExitSurvey = {
                            composeContainer.removeAllViews()
                            composeContainer.visibility = View.GONE
                            mapView.getMapAsync(this)
                            showFullRoute(getRouteResponse)
                        }
                    )
                }
                composeContainer.addView(composeView)
            }

            else -> null
        }
    }




    private fun getColorForTraffic(traffic: String): Int {
        return when (traffic) {
            "1" -> Color.GREEN  // 원활
            "2" -> Color.YELLOW // 서행
            "3" -> Color.HSVToColor(floatArrayOf(36f, 100f, 100f))// 지체
            "4" -> Color.RED    // 정체
            else -> Color.GRAY  // 알 수 없음
        }
    }

    private fun addCircleOverlay(point: Point, setcolor: Int, label: String) {
        val projection = naverMap.projection
        val metersPerDp = projection.metersPerDp       // 미터/DP 단위

        val circle = CircleOverlay().apply {
            center = LatLng(point.lat, point.lng)
            radius = metersPerDp * 10  // 미터 단위, 필요에 따라 조절
            outlineWidth = max(metersPerDp.toInt() * 2, 1)
            outlineColor = setcolor
            map = naverMap
            globalZIndex = 1000000
        }
        circleOverlays.add(circle)
    }

    // 네이버 맵 클리어
    private fun clearMap() {
        markers.forEach { it.map = null }
        markers.clear()

        pathOverlays.forEach { it.map = null }
        pathOverlays.clear()

        infoWindows.forEach { it.close() }
        infoWindows.clear()

        circleOverlays.forEach { it.map = null }
        circleOverlays.clear()
    }

    // 생명주기 메서드
    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}

@Composable
fun TopBar(
    onBackPressed: () -> Unit,
    onNextPressed: () -> Unit,
    showBack: Boolean,
    showNext: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(uicolor.White)
            .padding(8.dp)
    ) {
        if (showBack) {
            // 왼쪽 뒤로가기 버튼
            IconButton(onClick = onBackPressed,
                modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "뒤로가기"
                )
            }
        }

        // 중앙 제목
        Text(
            text = "경로",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.Center).padding(10.dp)
        )

        if(showNext) {
            // 오른쪽 다음 버튼
            IconButton(onClick = onNextPressed,
                modifier = Modifier.align(Alignment.CenterEnd) ) {
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = "다음"
                )
            }
        }
    }
}
