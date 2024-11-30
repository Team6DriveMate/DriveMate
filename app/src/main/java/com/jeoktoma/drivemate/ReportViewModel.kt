package com.jeoktoma.drivemate

import androidx.lifecycle.ViewModel

class ReportViewModel : ViewModel() {

//    private val surveyRequests = mutableListOf<SegmentSurveyRequest>()
//
//    // 예제 API 호출 함수
//    fun fetchSurveyRequests() {
//        viewModelScope.launch {
//            val response = withContext(Dispatchers.IO) {
//                // API 호출 코드
//                RetrofitInstance.surveyService.getSegmentSurveys() // 가정된 API 호출
//            }
//
//            if (response.isSuccessful) {
//                surveyRequests.clear()
//                surveyRequests.addAll(response.body() ?: emptyList())
//            }
//        }
//    }

    // Mock 데이터: 실제 API 호출로 데이터를 가져오도록 교체해야 함
    private val surveyRequests = listOf(
        SegmentSurveyRequest(
            sectionName = "구간 1",
            trafficCongestion = true,
            roadType = false,
            laneSwitch = true,
            situationDecision = false,
            laneConfusion = true,
            trafficLaws = false,
            tension = true
        ),
        SegmentSurveyRequest(
            sectionName = "구간 2",
            trafficCongestion = false,
            roadType = true,
            laneSwitch = false,
            situationDecision = true,
            laneConfusion = false,
            trafficLaws = true,
            tension = false
        ),
        SegmentSurveyRequest(
            sectionName = "구간 3",
            trafficCongestion = true,
            roadType = true,
            laneSwitch = false,
            situationDecision = false,
            laneConfusion = true,
            trafficLaws = false,
            tension = true
        )
    )

    /**
     * 특정 `segmentIndex`에 해당하는 `SegmentSurveyRequest` 데이터를 반환.
     * `segmentIndex`가 범위를 벗어나면 기본값 반환.
     */
    fun getSurveyRequestForSegment(segmentIndex: Int): SegmentSurveyRequest {
        return surveyRequests.getOrNull(segmentIndex)
            ?: SegmentSurveyRequest(
                sectionName = "알 수 없는 구간",
                trafficCongestion = false,
                roadType = false,
                laneSwitch = false,
                situationDecision = false,
                laneConfusion = false,
                trafficLaws = false,
                tension = false
            )
    }
}
