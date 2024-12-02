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


private val retrofit = Retrofit.Builder().baseUrl("http://43.203.232.158:8080/path/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val pathService = retrofit.create(PathService::class.java)

interface PathService {
    @POST("coord")
    suspend fun path(@Body coordRequest: CoordRequest): Response<CoordResponse>

    @POST("set")
    suspend fun setpath(@Body routeRequest: RouteRequest): Response<RouteResponse>
}

suspend fun performPathService(roadaddress: String, context: Context): CoordResponse? {
    return try {
        // IO 스레드에서 네트워크 요청 수행
        val response = withContext(Dispatchers.IO) {
            pathService.path(CoordRequest(roadaddress))
        }

        // 응답 성공 여부 확인 및 결과 처리
        if (response.isSuccessful) {
            response.body()  // 성공 시 CoordResponse 객체 반환
        } else {
            // 실패 시 UI 스레드에서 토스트 메시지 표시
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "서버 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
            }
            null  // 실패 시 null 반환
        }
    } catch (e: Exception) {
        // 네트워크 오류 발생 시 예외 처리 및 토스트 메시지 표시
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("Network Error", "Error: ${e.message}")
        }
        null  // 오류 발생 시 null 반환
    }
}

suspend fun setPathService(startLat:Double, startLng:Double, endLat:Double, endLng:Double, context: Context):RouteResponse?{
    return try {
        // IO 스레드에서 네트워크 요청 수행
        val response = withContext(Dispatchers.IO) {
            pathService.setpath(RouteRequest(Point(startLat, startLng), Point(endLat, endLng)))
        }

        // 응답 성공 여부 확인 및 결과 처리
        if (response.isSuccessful) {
            response.body()  // 성공 시 CoordResponse 객체 반환
        } else {
            // 실패 시 UI 스레드에서 토스트 메시지 표시
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "서버 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
            }
            null  // 실패 시 null 반환
        }
    } catch (e: Exception) {
        // 네트워크 오류 발생 시 예외 처리 및 토스트 메시지 표시
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("Network Error", "Error: ${e.message}")
        }
        null  // 오류 발생 시 null 반환
    }
}

