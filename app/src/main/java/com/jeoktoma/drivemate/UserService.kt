package com.jeoktoma.drivemate

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserService {
//    @POST("signup")
//    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<SignUpResponse>
//
//    @POST("login")
//    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("user/info/{username}")
    suspend fun getUserInfo(@Path("username") username: String): Response<UserInfoResponse>

    @POST("user/update/{username}")
    suspend fun updateUserInfo(
        @Path("username") username: String,
        @Body userUpdateRequest: UserUpdateRequest
    ): Response<SuccessResponse>
}
