package com.jeoktoma.drivemate

/*
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {
override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)
setContentView(R.layout.activity_signup)

val inputID = findViewById<EditText>(R.id.id_input)
val inputPW = findViewById<EditText>(R.id.password_input)
val checkPW = findViewById<EditText>(R.id.confirm_password_input)
val signUpButton = findViewById<Button>(R.id.signup_button)
val backToLogin = findViewById<TextView>(R.id.login_link)

signUpButton.setOnClickListener {
val id = inputID.text.toString()
val pw = inputPW.text.toString()
val confirmPw = checkPW.text.toString()

if (pw == confirmPw) {
performSignUp(id, pw)
} else {
Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
}
}

backToLogin.setOnClickListener {
finish() // 이 액티비티를 닫고 이전 화면으로 돌아갑니다.
}
}

private fun performSignUp(id: String, pw: String) {
// 회원가입 로직 추가
Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
}
}
*/