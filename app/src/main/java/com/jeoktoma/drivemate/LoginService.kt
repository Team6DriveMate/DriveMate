package com.jeoktoma.drivemate

import android.content.Context
import android.widget.Toast
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

suspend fun performLoginService(id: String, pw: String, context: Context): Boolean {
    return try {
        val response = loginService.login(LoginRequest(id, pw))
        response.isSuccessful && response.body()?.success == true
    } catch (e: Exception) {
        Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
        false
    }
}

