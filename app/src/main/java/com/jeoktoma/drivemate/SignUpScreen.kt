package com.jeoktoma.drivemate

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(navController: NavController) {
    var inputID by remember { mutableStateOf("") }
    var inputPW by remember { mutableStateOf("") }
    var checkPW by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("회원가입", fontSize = 32.sp)

        Spacer(modifier = Modifier.height(24.dp))

        BasicTextField(
            value = inputID,
            onValueChange = { inputID = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            decorationBox = { innerTextField ->
                Box(Modifier.padding(16.dp)) {
                    if (inputID.isEmpty()) Text("아이디 입력")
                    innerTextField()
                }
            }
        )

        BasicTextField(
            value = inputPW,
            onValueChange = { inputPW = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            decorationBox = { innerTextField ->
                Box(Modifier.padding(16.dp)) {
                    if (inputPW.isEmpty()) Text("비밀번호 입력")
                    innerTextField()
                }
            }
        )

        BasicTextField(
            value = checkPW,
            onValueChange = { checkPW = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            decorationBox = { innerTextField ->
                Box(Modifier.padding(16.dp)) {
                    if (checkPW.isEmpty()) Text("비밀번호 확인")
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            CoroutineScope(Dispatchers.Main).launch {
                if (inputPW == checkPW) {
                    val success = performSignUpService(id = inputID, pw = inputPW, context = context)
                    if (success) {
                        Toast.makeText(context, "계정 생성 성공", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, "계정 생성 실패", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text("회원가입")
        }


        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "로그인 화면으로 돌아가기",
            color = Color.Black,
            fontSize = 14.sp,
            modifier = Modifier.clickable {
                navController.popBackStack()
            }
        )
    }

}
