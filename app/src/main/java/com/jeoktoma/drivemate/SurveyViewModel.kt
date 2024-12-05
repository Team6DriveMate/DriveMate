// SurveyViewModel.kt
package com.jeoktoma.drivemate

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class DriveReportRequest(
    val startLocation: String,
    val endLocation: String,
    val startTime: String,
    val endTime: String
)

data class DriveReportResponse(
    val success: Boolean,
    val titleUpdate: Boolean,
    val titles: List<GetTitles>
)

data class GetTitles(
    val title: String,
    val dateObtained: String
)
class SurveyViewModel : ViewModel() {
    private val surveyService = RetrofitInstance.surveyService

    var switchLight: Int by mutableStateOf(0)
    var sideMirror: Int by mutableStateOf(0)
    var tension: Int by mutableStateOf(0)
    var weather: Int by mutableStateOf(0)
    var laneStaying: Int by mutableStateOf(0)
    var sightDegree: Int by mutableStateOf(0)
    var memo: String by mutableStateOf("")


    fun submitSegmentSurvey(
        segmentIndex: Int,
        surveyRequest: SegmentSurveyRequest,
        context: Context,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = surveyService.submitSegmentSurvey(segmentIndex, surveyRequest)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(context, "구간 설문 제출 성공", Toast.LENGTH_SHORT).show()
                        onSuccess()
                    } else {
                        Toast.makeText(context, "구간 설문 제출 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
//                    Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun submitOverallSurvey(
        surveyRequest: OverallSurveyRequest,
        context: Context,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = surveyService.submitOverallSurvey(surveyRequest)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        //Toast.makeText(context, "전체 설문 제출 성공", Toast.LENGTH_SHORT).show()
                        onSuccess()
                    } else {
                        Toast.makeText(context, "전체 설문 제출 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun submitDriveReport(
        reportRequest: DriveReportRequest,
        context: Context,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = surveyService.submitDriveReport(reportRequest)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(context, "운행 기록 제출 성공", Toast.LENGTH_SHORT).show()
                        onSuccess()
                    } else {
                        Toast.makeText(context, "운행 기록 제출 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
