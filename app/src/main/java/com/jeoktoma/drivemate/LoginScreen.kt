package com.jeoktoma.drivemate

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
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
            .padding(16.dp),
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
