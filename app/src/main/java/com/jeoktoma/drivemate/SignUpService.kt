package com.jeoktoma.drivemate

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

private val retrofit = Retrofit.Builder().baseUrl("http://3.34.127.76:8080/user/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val signUpService = retrofit.create(SignUpService::class.java)

interface SignUpService {
    @POST("signup")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<SignUpResponse>
}

suspend fun performSignUpService(nickname:String, username: String, pw: String, context: Context): Boolean {
    return try {
        withContext(Dispatchers.IO) {
            val response = signUpService.signUp(SignUpRequest(nickname, username, pw, pw))
            if (response.isSuccessful && response.body()?.success == true) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "회원가입 성공", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "회원가입 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
                false
            }
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            Log.e("signup", "${e.message}")
            Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        false
    }
}
//
//suspend fun performSignUpService(id: String, pw: String, context: Context): Boolean {
//    return try {
//        withContext(Dispatchers.IO) {
//            val response = SignUpService.signUp(SignUpRequest(id, pw))
//            if (response.isSuccessful && response.body()?.success == true) {
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(context, "회원가입 성공", Toast.LENGTH_SHORT).show()
//                }
//                true
//            } else {
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(context, "회원가입 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
//                }
//                false
//            }
//        }
//    } catch (e: Exception) {
//        withContext(Dispatchers.Main) {
//            Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
//        }
//        false
//    }
//}


//suspend fun performSignUpService(id: String, pw: String, context: Context): Boolean {
//    return withContext(Dispatchers.IO) {
//        try {
//            val response = RetrofitClient.instance.signUp(SignUpRequest(id = id, password = pw))
//            if (response.success) {
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(context, "계정 생성 성공", Toast.LENGTH_SHORT).show()
//                }
//                true
//            } else {
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(context, response.message ?: "회원가입 실패", Toast.LENGTH_SHORT).show()
//                }
//                false
//            }
//        } catch (e: Exception) {
//            withContext(Dispatchers.Main) {
//                Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//            false
//        }
//    }
//}

