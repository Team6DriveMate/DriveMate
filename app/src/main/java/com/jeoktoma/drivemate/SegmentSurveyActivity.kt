package com.jeoktoma.drivemate

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.naver.maps.geometry.LatLng

class SegmentSurveyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Intent로 전달받은 데이터 처리
        val roadName = intent.getStringExtra("roadName") ?: "Unknown Road"
        val segmentIndex = intent.getIntExtra("segmentIndex", 0)
        val totalSegments = intent.getIntExtra("totalSegments", 1)
        val segmentCoords = intent.getParcelableArrayExtra("segmentCoords")?.map { it as LatLng } ?: listOf()

        // SurveyViewModel 인스턴스 생성
        val surveyViewModel = SurveyViewModel()

//        setContent {
//            SegmentSurveyScreen(
//                roadName = roadName,
//                segmentIndex = segmentIndex,
//                totalSegments = totalSegments,
//                surveyViewModel = surveyViewModel,
//                context = this,
////                navController = null, // NavController는 필요에 따라 추가 설정
//                //segmentCoords = segmentCoords // 추가된 경로 정보 전달
//            )
//        }
    }
}
