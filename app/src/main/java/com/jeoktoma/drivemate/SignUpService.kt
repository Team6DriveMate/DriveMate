package com.jeoktoma.drivemate

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class SignUpRequest(val id: String, val password: String)

data class SignUpResponse(val success: Boolean, val message: String?)

interface SignUpApi {
    @POST("/signup")
    suspend fun signUp(@Body request: SignUpRequest): SignUpResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://9a16ddb0-40b6-4586-9705-5429842da0f1.mock.pstmn.io/user/"

    val instance: SignUpApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SignUpApi::class.java)
    }
}

suspend fun performSignUpService(id: String, pw: String, context: Context): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.instance.signUp(SignUpRequest(id = id, password = pw))
            if (response.success) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "계정 생성 성공", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, response.message ?: "회원가입 실패", Toast.LENGTH_SHORT).show()
                }
                false
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            false
        }
    }
}
