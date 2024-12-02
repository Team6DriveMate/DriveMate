package com.jeoktoma.drivemate

data class ReportListResponse(
    val reports: List<Report>
)

data class Report(
    val reportId: Int,
    val title: String,
    val date: String,
    val time: String,
    val distance: Double
)

data class DetailReportResponse(
    val driveId: String,
    val startLocation: String,
    val endLocation: String,
    val startTime: String,
    val endTime: String,
    val distance: String,
    val timeTaken: String,
    val segmentSurveys: List<SegmentSurvey>,
    val overallSurvey: OverallSurvey,
    val weakPoints: List<String>,
    val path: Path
)

data class SegmentSurvey(
    val segmentName: String,
    val trafficCongestion: Boolean,
    val roadType: Boolean,
    val laneSwitch: Boolean,
    val situationDecision: Boolean,
    val laneConfusion: Boolean,
    val trafficLaws: Boolean,
    val tension: Boolean,
    val segmentIndex: Int
)

data class OverallSurvey(
    val switchLight: Int,
    val sideMirror: Int,
    val tension: Int,
    val weather: Int,
    val laneStaying: Int,
    val sightDegree: Int,
    val memo: String
)

data class Path(
    val totalDistance: Int,
    val totalTime: Int,
    val route: Route
)

data class readResponse(val success: Boolean)
