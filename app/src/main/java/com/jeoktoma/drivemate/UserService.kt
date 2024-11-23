package com.jeoktoma.drivemate

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

private val retrofit = Retrofit.Builder()
    .baseUrl("https://3f7e2b24-1cc2-47ea-992c-ea0009e1538c.mock.pstmn.io/user/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val userService = retrofit.create(UserService::class.java)
interface UserService {
    @POST("signup")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<SignUpResponse>

    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
    @GET("info/{username}")
    suspend fun getUserInfo(@Path("username") username: String): Response<UserInfoResponse>

    @POST("update/{username}")
    suspend fun updateUserInfo(@Path("username") username: String, @Body request: UserUpdateRequest): Response<SuccessResponse>
}

data class UserInfoResponse(
    val nickname: String,
    val username: String,
    val experience: Int,
    val nextLevelExperience: Int,
    val mainTitle: String,
    val weakPoint: List<String>,
    val titles: List<TitleDTO>
)
data class TitleDTO(val name: String, val dateObtained: String)
data class UserUpdateRequest(val nickname: String, val mainTitle: String)
data class SuccessResponse(val success: Boolean)