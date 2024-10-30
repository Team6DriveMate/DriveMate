package com.jeoktoma.drivemate

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

private val retrofit = Retrofit.Builder().baseUrl("https://9a16ddb0-40b6-4586-9705-5429842da0f1.mock.pstmn.io/user/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val loginService = retrofit.create(LoginService::class.java)

interface LoginService {
    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
}


fun performLogin(id: String, pw: String, context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        val response = loginService.login(LoginRequest(id, pw))
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                val loginResponse = response.body()
                if (loginResponse?.success == true) {
                    // 로그인 성공 처리
                    Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()
                } else {
                    // 로그인 실패 처리
                    Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            } else {
                // 네트워크 오류 처리
                Toast.makeText(context, "네트워크 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
