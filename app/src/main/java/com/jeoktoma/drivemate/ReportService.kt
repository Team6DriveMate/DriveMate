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
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


private val retrofit = Retrofit.Builder().baseUrl("http://10.0.2.2:8080/report/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val reportService = retrofit.create(ReportService::class.java)

interface ReportService {
    @POST("complete")
    suspend fun complete(@Body completeRequest: CompleteRequest): Response<CompleteResponse>

    @GET("route")
    suspend fun route(): Response<GetRouteResponse>

    @GET("list")
    suspend fun list(): Response<ReportListResponse>

    @GET("{reportId}")
    suspend fun detailReport(@Path("reportId") reportId: Int): Response<DetailReportResponse>

    @POST("read")
    suspend fun readReport(): Response<readResponse>
}
suspend fun performCompleteService(start_location:Point, end_location:Point, stopover_location:List<Point>?,
                                 context:Context): CompleteResponse? {
    return try {
        // IO 스레드에서 네트워크 요청 수행
        val response = withContext(Dispatchers.IO) {
            reportService.complete(CompleteRequest(start_location, end_location, stopover_location))
        }

        // 응답 성공 여부 확인 및 결과 처리
        if (response.isSuccessful) {
            response.body()  // 성공 시 CoordResponse 객체 반환
        } else {
            // 실패 시 UI 스레드에서 토스트 메시지 표시
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "서버 오류: ${response.code()}", Toast.LENGTH_LONG).show()
            }
            null  // 실패 시 null 반환
        }
    } catch (e: Exception) {
        // 네트워크 오류 발생 시 예외 처리 및 토스트 메시지 표시
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_LONG).show()
        }
        null  // 오류 발생 시 null 반환
    }
}

suspend fun performGetRouteService(context: Context): GetRouteResponse?{
    return try {
        // IO 스레드에서 네트워크 요청 수행
        val response = withContext(Dispatchers.IO) {
            reportService.route()
        }

        // 응답 성공 여부 확인 및 결과 처리
        if (response.isSuccessful) {
            response.body()  // 성공 시 CoordResponse 객체 반환
        } else {
            // 실패 시 UI 스레드에서 토스트 메시지 표시
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "서버 오류: ${response.code()}", Toast.LENGTH_LONG).show()
            }
            null  // 실패 시 null 반환
        }
    } catch (e: Exception) {
        // 네트워크 오류 발생 시 예외 처리 및 토스트 메시지 표시
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_LONG).show()
        }
        null  // 오류 발생 시 null 반환
    }
}

suspend fun getReportList(context: Context): ReportListResponse?{
    return try {
        // IO 스레드에서 네트워크 요청 수행
        val response = withContext(Dispatchers.IO) {
            reportService.list()
        }

        // 응답 성공 여부 확인 및 결과 처리
        if (response.isSuccessful) {
            response.body()  // 성공 시 CoordResponse 객체 반환
        } else {
            // 실패 시 UI 스레드에서 토스트 메시지 표시
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "서버 오류: ${response.code()}", Toast.LENGTH_LONG).show()
            }
            null  // 실패 시 null 반환
        }
    } catch (e: Exception) {
        // 네트워크 오류 발생 시 예외 처리 및 토스트 메시지 표시
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_LONG).show()
        }
        null  // 오류 발생 시 null 반환
    }
}

suspend fun getReportDetail(reportId:Int,context: Context): DetailReportResponse?{
    return try {
        // IO 스레드에서 네트워크 요청 수행
        val response = withContext(Dispatchers.IO) {
            reportService.detailReport(reportId)
        }

        // 응답 성공 여부 확인 및 결과 처리
        if (response.isSuccessful) {
            response.body()  // 성공 시 CoordResponse 객체 반환
        } else {
            // 실패 시 UI 스레드에서 토스트 메시지 표시
            withContext(Dispatchers.Main) {
                Log.e("ReportService", "서버 오류: ${response.code()}")
                Toast.makeText(context, "서버 오류: ${response.code()}", Toast.LENGTH_LONG).show()
            }
            null  // 실패 시 null 반환
        }
    } catch (e: Exception) {
        // 네트워크 오류 발생 시 예외 처리 및 토스트 메시지 표시
        withContext(Dispatchers.Main) {
            Log.e("ReportService", "네트워크 오류: ${e.message}")
            Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_LONG).show()
        }
        null  // 오류 발생 시 null 반환
    }
}

suspend fun performReadReport(context:Context):Boolean {
    return try {
        withContext(Dispatchers.IO) {
            val response = reportService.readReport()
            if (response.isSuccessful && response.body()?.success == true) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "리포트 읽기 완료", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "리포트 읽기 실패: ${response.code()}", Toast.LENGTH_SHORT)
                        .show()
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
