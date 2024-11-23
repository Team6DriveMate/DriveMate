// SurveyService.kt
package com.jeoktoma.drivemate

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface SurveyService {
    @POST("report/survey/{segmentIndex}")
    suspend fun submitSegmentSurvey(
        @Path("segmentIndex") segmentIndex: Int,
        @Body request: SegmentSurveyRequest
    ): Response<SuccessResponse>

    @POST("report/survey/route")
    suspend fun submitOverallSurvey(
        @Body request: OverallSurveyRequest
    ): Response<SuccessResponse>
}
