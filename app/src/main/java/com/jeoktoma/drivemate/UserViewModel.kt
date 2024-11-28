package com.jeoktoma.drivemate

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel : ViewModel() {
    var userInfo: UserInfoResponse? = null

    var username by mutableStateOf("")
    private set
    var nickname by mutableStateOf("")
    private set
    var title by mutableStateOf("")
    private set



    // 유저 정보 조회
    fun getUserInfo(username: String, context: Context, onResult: (UserInfoResponse?) -> Unit) {
        viewModelScope.launch {
            try {
                val url = "http://10.0.2.2/user/info/$username"
                Log.d("UserViewModel", "API 호출 URL: $url")

                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.userService.getUserInfo(username)
                }
                if (response.isSuccessful) {
                    userInfo = response.body()
                    this@UserViewModel.username = username
                    nickname = userInfo?.nickname ?: ""
                    title = userInfo?.mainTitle?: ""
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

    // 레벨업 및 경험치 업데이트
    fun updateLevelAndExperience(context: Context, username: String) {
        userInfo?.let { user ->
            val updatedExperience = user.experience + 50 // 주행 완료 시 경험치 추가
            val experienceForNextLevel = user.nextLevelExperience

            val isLevelUp = updatedExperience >= experienceForNextLevel
            val newExperience = if (isLevelUp) updatedExperience % experienceForNextLevel else updatedExperience
            val newLevelExperience = if (isLevelUp) experienceForNextLevel + 50 else experienceForNextLevel // 다음 레벨 경험치 증가 로직

            // 업데이트된 유저 데이터를 UserInfoResponse를 기반으로 업데이트
            val updatedUserInfo = user.copy(
                experience = newExperience,
                nextLevelExperience = newLevelExperience
            )

            // 새로운 UserUpdateRequest 생성
            val updateRequest = UserUpdateRequest(
                nickname = user.nickname,
                mainTitle = user.mainTitle
            )

            // 백엔드로 업데이트 요청
            updateUserInfo(username, updateRequest, context, onSuccess = {
                // 성공 시 로컬 데이터 업데이트
                userInfo = updatedUserInfo
                if (isLevelUp) {
                    Toast.makeText(context, "레벨 업 성공!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "경험치가 추가되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }, onError = {
                Toast.makeText(context, "레벨 업 실패", Toast.LENGTH_SHORT).show()
            })
        }
    }



}
