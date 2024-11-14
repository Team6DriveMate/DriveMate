package com.jeoktoma.drivemate

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
fun LoginScreen(navController: NavController) {
    var inputID by remember { mutableStateOf("") }
    var inputPW by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .statusBarsPadding() // 상태바 공간을 제거하고 레이아웃을 꽉 채움
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("DriveMate", fontSize = 32.sp)

        Spacer(modifier = Modifier.height(24.dp))

        BasicTextField(
            value = inputID,
            onValueChange = { inputID = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            decorationBox = { innerTextField ->
                Box(Modifier.padding(16.dp)) {
                    if (inputID.isEmpty()) Text("아이디")
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
                    if (inputPW.isEmpty()) Text("비밀번호")
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            CoroutineScope(Dispatchers.Main).launch {
                val success = performLoginService(inputID, inputPW, context)
                if (success) {
                    navController.navigate("mainScreen")
                }
            }
        }) {
            Text("로그인")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "계정이 없다면? 회원가입 하기",
            color = Color.Blue,
            fontSize = 14.sp,
            modifier = Modifier.clickable {
                navController.navigate("signUpScreen")
            }
        )
    }
}
