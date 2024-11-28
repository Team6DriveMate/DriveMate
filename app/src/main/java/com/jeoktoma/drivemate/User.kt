package com.jeoktoma.drivemate

data class User(
    val id:String,
    val pw:String
)
data class UserInfoResponse(
    val nickname: String,
    val username: String,
    val mainTitle: String,
    val level: Int,
    val experience: Int,
    val nextLevelExperience: Int,
    val weakPoints: List<String>,
    val titles: List<Title>
)

data class Title(
    val name: String,
    val dateObtained: String
)

data class UserUpdateRequest(val nickname: String, val mainTitle: String)
data class SuccessResponse(val success: Boolean)

data class LoginRequest(val username: String, val pw: String)
data class LoginResponse(val success: Boolean)
data class SignUpRequest(val nickname: String, val username: String, val pw: String, val confirmPw: String)
data class SignUpResponse(val success: Boolean)


