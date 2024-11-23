package com.jeoktoma.drivemate

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel : ViewModel() {
    var isLoggedIn = mutableStateOf(false)

    fun signUp(id: String, pw: String, confirmPw: String, context: Context) {
        if (pw != confirmPw) {
            Toast.makeText(context, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = userService.signUp(SignUpRequest(id, pw))
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(context, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "회원가입 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun login(id: String, pw: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = userService.login(LoginRequest(id, pw))
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        isLoggedIn.value = true
                        Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()
                    } else {
                        isLoggedIn.value = false
                        Toast.makeText(context, "로그인 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
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
