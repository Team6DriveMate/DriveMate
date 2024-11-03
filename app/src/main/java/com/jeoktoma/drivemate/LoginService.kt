package com.jeoktoma.drivemate

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

private val retrofit = Retrofit.Builder().baseUrl("https://3f7e2b24-1cc2-47ea-992c-ea0009e1538c.mock.pstmn.io/user/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val loginService = retrofit.create(LoginService::class.java)

interface LoginService {
    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
}

suspend fun performLoginService(id: String, pw: String, context: Context): Boolean {
    return try {
        withContext(Dispatchers.IO) {
            val response = loginService.login(LoginRequest(id, pw))
            if (response.isSuccessful && response.body()?.success == true) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
                false
            }
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        false
    }
}



//fun performLoginService(id: String, pw: String, context: Context) {
//    CoroutineScope(Dispatchers.IO).launch {
//        val response = loginService.login(LoginRequest(id, pw))
//        withContext(Dispatchers.Main) {
//            if (response.isSuccessful) {
//                val loginResponse = response.body()
//                if (loginResponse?.success == true) {
//                    // 로그인 성공 처리
//                    Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()
//                } else {
//                    // 로그인 실패 처리
//                    Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                // 네트워크 오류 처리
//                Toast.makeText(context, "네트워크 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}


