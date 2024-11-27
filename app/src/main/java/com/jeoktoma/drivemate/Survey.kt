package com.jeoktoma.drivemate

data class SegmentSurveyRequest(
    val sectionName: String,
    val trafficCongestion: Boolean,
    val roadType: Boolean,
    val laneSwitch: Boolean,
    val situationDecision: Boolean,
    val laneConfusion: Boolean,
    val trafficLaws: Boolean,
    val tension: Boolean
)

data class OverallSurveyRequest(
    val switchLight: Int,
    val sideMirror: Int,
    val tension: Int,
    val weather: Int,
    val laneStaying: Int,
    val sightDegree: Int,
    val memo: String
)

//data class SuccessResponse(
//    val success: Boolean
//)