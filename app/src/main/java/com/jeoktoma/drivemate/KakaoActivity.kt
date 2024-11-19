package com.jeoktoma.drivemate

import android.graphics.Color
import android.icu.util.TimeZone
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.kakaomobility.knsdk.KNRoutePriority
import com.kakaomobility.knsdk.common.objects.KNError
import com.kakaomobility.knsdk.common.objects.KNPOI
import com.kakaomobility.knsdk.guidance.knguidance.KNGuidance
import com.kakaomobility.knsdk.guidance.knguidance.KNGuidance_CitsGuideDelegate
import com.kakaomobility.knsdk.guidance.knguidance.KNGuidance_GuideStateDelegate
import com.kakaomobility.knsdk.guidance.knguidance.KNGuidance_LocationGuideDelegate
import com.kakaomobility.knsdk.guidance.knguidance.KNGuidance_RouteGuideDelegate
import com.kakaomobility.knsdk.guidance.knguidance.KNGuidance_SafetyGuideDelegate
import com.kakaomobility.knsdk.guidance.knguidance.KNGuidance_VoiceGuideDelegate
import com.kakaomobility.knsdk.guidance.knguidance.KNGuideRouteChangeReason
import com.kakaomobility.knsdk.guidance.knguidance.citsguide.KNGuide_Cits
import com.kakaomobility.knsdk.guidance.knguidance.common.KNLocation
import com.kakaomobility.knsdk.guidance.knguidance.locationguide.KNGuide_Location
import com.kakaomobility.knsdk.guidance.knguidance.routeguide.KNGuide_Route
import com.kakaomobility.knsdk.guidance.knguidance.routeguide.objects.KNMultiRouteInfo
import com.kakaomobility.knsdk.guidance.knguidance.safetyguide.KNGuide_Safety
import com.kakaomobility.knsdk.guidance.knguidance.safetyguide.objects.KNSafety
import com.kakaomobility.knsdk.guidance.knguidance.voiceguide.KNGuide_Voice
import com.kakaomobility.knsdk.trip.kntrip.KNTrip
import com.kakaomobility.knsdk.trip.kntrip.knroute.KNRoute
import com.kakaomobility.knsdk.ui.view.KNNaviView
import android.widget.Toast
import android.os.Handler
import android.os.Looper
import com.kakaomobility.knsdk.common.gps.KATECToWGS84
import com.kakaomobility.knsdk.common.gps.WGS84ToKATEC
import com.kakaomobility.knsdk.common.util.DoublePoint

class KakaoActivity : AppCompatActivity(), KNGuidance_GuideStateDelegate, KNGuidance_LocationGuideDelegate,
    KNGuidance_RouteGuideDelegate, KNGuidance_SafetyGuideDelegate, KNGuidance_VoiceGuideDelegate,
    KNGuidance_CitsGuideDelegate{
    lateinit var naviView: KNNaviView

    private var changedLocations = mutableListOf<LocInfo>()
    private val katecCoordinates = mutableListOf<DoublePoint>()
    private val times = mutableListOf<Long>()

    private var poidx = 1

    data class LocInfo(
        val lat: Double,
        val lng: Double
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kakao)

        naviView = findViewById(R.id.navi_view)

        // Intent에서 위도와 경도 리스트 받기
        val latitudes = intent.getSerializableExtra("lat_list") as? ArrayList<Double>
        val longitudes = intent.getSerializableExtra("lng_list") as? ArrayList<Double>

        if (latitudes != null && longitudes != null) {

            for (i in latitudes.indices) {
                // 각 위도/경도 쌍에 대해 WGS84 -> KATEC 변환 수행
                val wgsLat = latitudes[i]
                val wgsLng = longitudes[i]

                // WGS84 -> KATEC 변환
                val katecCoord = WGS84ToKATEC(wgsLng, wgsLat)

                // 변환된 KATEC 좌표를 리스트에 추가
                katecCoordinates.add(katecCoord)
                println("위도: ${katecCoordinates[i].x}, 경도: ${katecCoordinates[i].y}")
            }

            times.add(System.currentTimeMillis())

        // status bar 영역까지 사용하기 위한 옵션
        window?.apply {
            statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        requestRoute()


        }
    }

    /**
     * 주행 경로를 요청합니다.
     */
    fun requestRoute() {
        Thread {
            // 출발지와 목적지를 설정합니다.
            val startPoi = KNPOI("현위치", katecCoordinates.first().x.toInt(), katecCoordinates.first().y.toInt(),"현위치")
            val goalPoi = KNPOI("목적지",katecCoordinates.last().x.toInt(),katecCoordinates.last().y.toInt(),"목적지")

            KakaoNavi.knsdk.makeTripWithStart(
                aStart = startPoi,
                aGoal = goalPoi,
                aVias = null
            ) { aError, aTrip ->

                // 경로 요청이 성공하면 aError는 Null이 됩니다.
                if (aError == null) {
                    startGuide(aTrip)
                }
            }
        }.start()
    }

    fun startGuide(trip: KNTrip?) {
        KakaoNavi.knsdk.sharedGuidance()?.apply {
            // guidance delegate 등록
            guideStateDelegate = this@KakaoActivity
            locationGuideDelegate = this@KakaoActivity
            routeGuideDelegate = this@KakaoActivity
            safetyGuideDelegate = this@KakaoActivity
            voiceGuideDelegate = this@KakaoActivity
            citsGuideDelegate = this@KakaoActivity

            naviView.initWithGuidance(
                this,
                trip,
                KNRoutePriority.KNRoutePriority_Recommand,
                0
            )
        }
    }

    override fun guidanceCheckingRouteChange(aGuidance: KNGuidance) {
        naviView.guidanceCheckingRouteChange(aGuidance)
    }

    override fun guidanceDidUpdateRoutes(aGuidance: KNGuidance, aRoutes: List<KNRoute>, aMultiRouteInfo: KNMultiRouteInfo?) {
        naviView.guidanceDidUpdateRoutes(aGuidance, aRoutes, aMultiRouteInfo)
        // 루트가 변했을 경우
//        대안 경로 선택 또는 다른 경로를 선택하였을 경우
//                자동 재탐색 시 경로가 변경된 경우
//                수동 재탐색 시 경로가 변경된 경우
        if(aGuidance.locationGuide?.location?.pos != null) {
            val wgsloc = KATECToWGS84(
                aGuidance.locationGuide?.location?.pos!!.x,
                aGuidance.locationGuide?.location?.pos!!.y
            )
            changedLocations.add(LocInfo(wgsloc.x, wgsloc.y))

            Toast.makeText(
                this,
                "경로 벗어남! 위도: ${changedLocations.last().lat}, 경도: ${changedLocations.last().lng}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun guidanceGuideEnded(aGuidance: KNGuidance) {
        naviView.guidanceGuideEnded(aGuidance)
    }

    override fun guidanceGuideStarted(aGuidance: KNGuidance) {
        naviView.guidanceGuideStarted(aGuidance)
    }

    override fun guidanceOutOfRoute(aGuidance: KNGuidance) {
        naviView.guidanceOutOfRoute(aGuidance)
    }

    override fun guidanceRouteChanged(
        aGuidance: KNGuidance,
        aFromRoute: KNRoute,
        aFromLocation: KNLocation,
        aToRoute: KNRoute,
        aToLocation: KNLocation,
        aChangeReason: KNGuideRouteChangeReason
    ) {
        naviView.guidanceRouteChanged(aGuidance)
    }

    fun guidanceRouteChanged(aGuidance: KNGuidance) {
        naviView.guidanceRouteChanged(aGuidance)
    }

    override fun guidanceRouteUnchanged(aGuidance: KNGuidance) {
        naviView.guidanceRouteUnchanged(aGuidance)
    }

    override fun guidanceRouteUnchangedWithError(aGuidnace: KNGuidance, aError: KNError) {
        naviView.guidanceRouteUnchangedWithError(aGuidnace, aError)
    }

    override fun guidanceDidUpdateLocation(aGuidance: KNGuidance, aLocationGuide: KNGuide_Location) {
        naviView.guidanceDidUpdateLocation(aGuidance, aLocationGuide)

        // 위치가 변경될 때 마다 호출
        val wgspoi = KATECToWGS84(aLocationGuide.location?.pos!!.x, aLocationGuide.location?.pos!!.y)
        val nextwgs = KATECToWGS84(katecCoordinates[poidx].x, katecCoordinates[poidx].y)
        val distanceArray = FloatArray(1)
        Location.distanceBetween(wgspoi.x, wgspoi.y, nextwgs.x, nextwgs.y, distanceArray)
        // 포인트와의 거리가 50m 미만
        if(distanceArray[0] < 50){
            Toast.makeText(this, "$poidx 포인트 도착", Toast.LENGTH_LONG).show()
            times.add(System.currentTimeMillis())
            poidx += 1
        }
        else{
            Toast.makeText(this, "$poidx 포인트까지 ${distanceArray[0]} 미터", Toast.LENGTH_SHORT).show()
        }
    }

    override fun guidanceDidUpdateRouteGuide(aGuidance: KNGuidance, aRouteGuide: KNGuide_Route) {
        naviView.guidanceDidUpdateRouteGuide(aGuidance, aRouteGuide)
    }

    override fun guidanceDidUpdateAroundSafeties(aGuidance: KNGuidance, aSafeties: List<KNSafety>?) {
        naviView.guidanceDidUpdateAroundSafeties(aGuidance, aSafeties)
    }

    override fun guidanceDidUpdateSafetyGuide(aGuidance: KNGuidance, aSafetyGuide: KNGuide_Safety?) {
        naviView.guidanceDidUpdateSafetyGuide(aGuidance, aSafetyGuide)
    }

    override fun didFinishPlayVoiceGuide(aGuidance: KNGuidance, aVoiceGuide: KNGuide_Voice) {
        naviView.didFinishPlayVoiceGuide(aGuidance, aVoiceGuide)
    }

    override fun shouldPlayVoiceGuide(aGuidance: KNGuidance, aVoiceGuide: KNGuide_Voice, aNewData: MutableList<ByteArray>): Boolean {
        return naviView.shouldPlayVoiceGuide(aGuidance, aVoiceGuide, aNewData)
    }

    override fun willPlayVoiceGuide(aGuidance: KNGuidance, aVoiceGuide: KNGuide_Voice) {
        naviView.willPlayVoiceGuide(aGuidance, aVoiceGuide)
    }

    override fun didUpdateCitsGuide(aGuidance: KNGuidance, aCitsGuide: KNGuide_Cits) {
        naviView.didUpdateCitsGuide(aGuidance, aCitsGuide)
    }


}


