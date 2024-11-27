package com.jeoktoma.drivemate

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://3f7e2b24-1cc2-47ea-992c-ea0009e1538c.mock.pstmn.io/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val userService: UserService = retrofit.create(UserService::class.java)
    val surveyService: SurveyService = retrofit.create(SurveyService::class.java)
}