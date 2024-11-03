package com.jeoktoma.drivemate

data class User(
    val id:String,
    val pw:String
)

data class LoginRequest(val id: String, val pw: String)
data class LoginResponse(val success: Boolean)
data class SignUpRequest(val id: String, val pw: String)
data class SignUpResponse(val success: Boolean)

