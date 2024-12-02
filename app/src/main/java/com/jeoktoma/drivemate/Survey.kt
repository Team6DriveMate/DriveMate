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

/*
public class SegmentSurveyDTO {
    private String segmentName;
    private Boolean trafficCongestion;
    private Boolean roadType;
    private Boolean laneSwitch;
    private Boolean situationDecision;
    private Boolean trafficLaws;
    private Boolean tensions;
    private Boolean laneConfusion;
}

 */


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