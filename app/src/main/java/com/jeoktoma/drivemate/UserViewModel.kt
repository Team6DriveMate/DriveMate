package com.jeoktoma.drivemate

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel : ViewModel() {
//    var userInfo: UserInfoResponse? = null
    var userInfo: UserInfoResponse? = null
        private set

    var username by mutableStateOf("")
    private set
    var nickname by mutableStateOf("")
    private set
    var title by mutableStateOf("")
    private set

    var level by mutableStateOf(0)
    private set
    var experience by mutableStateOf(0)
        private set

    var weakPoints by mutableStateOf(listOf("0"))

    var titles by mutableStateOf(listOf(Title("", "")))



    // 유저 정보 조회
    fun getUserInfo(username: String, context: Context, onResult: (UserInfoResponse?) -> Unit) {
        viewModelScope.launch {
            try {
                val url = "http://43.203.232.158:8080/user/info/$username"
                Log.d("UserViewModel", "API 호출 URL: $url")

                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.userService.getUserInfo(username)
                }
                if (response.isSuccessful) {
                    userInfo = response.body()
                    this@UserViewModel.username = username
                    nickname = userInfo?.nickname ?: ""
                    title = userInfo?.mainTitle?: ""
                    this@UserViewModel.level = userInfo?.level?:0
                    this@UserViewModel.experience = userInfo?.experience?:0
                    this@UserViewModel.weakPoints = (userInfo?.weakPoint?:listOf("아직 취약점 데이터가 없습니다.")).map { weakpoint ->
                        when (weakpoint) {
                            "switchLight" -> "방향등"
                            "sideMirror" -> "사이드미러"
                            "tension" -> "긴장도"
                            "weather" -> "날씨"
                            "laneStaying" -> "차선 유지"
                            "laneSwitch" -> "차선 변경"
                            "laneConfusion" -> "차선 혼동"
                            "trafficLaws" -> "교통 법규 준수"
                            "situationDecision" -> "상황 판단"
                            "sightDegree" -> "시야각"
                            "trafficCongestion" -> "혼잡도"
                            "roadType" -> "도로 유형"
                            else -> "아직 취약점 데이터가 없습니다."
                        }
                    }
                    this@UserViewModel.titles = userInfo?.titles?: listOf(Title("", ""))
                    onResult(userInfo)
                } else {
                    Toast.makeText(context, "유저 정보 조회 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                    onResult(null)
                }
            } catch (e: Exception) {
                Log.e("EditProfile", "${e.message}")
                Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                onResult(null)
            }
        }
    }

    // 유저 정보 업데이트
    fun updateUserInfo(
        username: String,
        userUpdateRequest: UserUpdateRequest,
        context: Context,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.userService.updateUserInfo(username, userUpdateRequest)
                }
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(context, "유저 정보 업데이트 성공", Toast.LENGTH_SHORT).show()
                    onSuccess()
                } else {
                    Toast.makeText(context, "유저 정보 업데이트 실패", Toast.LENGTH_SHORT).show()
                    onError()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                onError()
            }
        }
    }


    fun clearUserData() {
        userInfo = null
    }

}

fun performLogout(navController: NavController, viewModel: UserViewModel) {
    // 사용자 정보 초기화
    viewModel.clearUserData()
    // 로그인 화면으로 이동
    navController.navigate("loginScreen") {
        popUpTo(0) { inclusive = true } // 스택 초기화
    }
}
