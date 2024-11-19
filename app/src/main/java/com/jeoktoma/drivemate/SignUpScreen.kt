package com.jeoktoma.drivemate

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "회원가입",
            fontSize = 24.sp,
            fontFamily = FontFamily(Font(R.font.poppins)),
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("이름", fontFamily = FontFamily(Font(R.font.poppins)),) },
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.component_1_ic_user), contentDescription = "User Icon")
            },
            shape = RoundedCornerShape(14.dp), // 모서리를 둥글게 설정
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color(0xFFF7F8F8), // 배경색 설정
                focusedBorderColor = Color.Transparent, // 포커스 시 테두리 제거
                unfocusedBorderColor = Color.Transparent // 비포커스 시 테두리 제거
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("아이디", fontFamily = FontFamily(Font(R.font.poppins)),) },
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.component_1_ic_user), contentDescription = "User Icon")
            },
            shape = RoundedCornerShape(14.dp), // 모서리를 둥글게 설정
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color(0xFFF7F8F8), // 배경색 설정
                focusedBorderColor = Color.Transparent, // 포커스 시 테두리 제거
                unfocusedBorderColor = Color.Transparent // 비포커스 시 테두리 제거
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호", fontFamily = FontFamily(Font(R.font.poppins)),) },
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.component_1_ic_lock), contentDescription = "Password Icon")
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(id = if (passwordVisible) R.drawable.component_1_ic_visibility else R.drawable.component_1_ic_visibility_off),
                        contentDescription = "Toggle Password Visibility"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(14.dp), // 모서리를 둥글게 설정
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color(0xFFF7F8F8), // 배경색 설정
                focusedBorderColor = Color.Transparent, // 포커스 시 테두리 제거
                unfocusedBorderColor = Color.Transparent // 비포커스 시 테두리 제거
            ),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None, // 자동 대문자 비활성화
                keyboardType = KeyboardType.Text,            // 일반 텍스트 키보드
                imeAction = ImeAction.Done                   // 완료 버튼 사용
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("비밀번호 확인", fontFamily = FontFamily(Font(R.font.poppins)),) },
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.component_1_ic_lock), contentDescription = "Confirm Password Icon")
            },
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        painter = painterResource(id = if (confirmPasswordVisible) R.drawable.component_1_ic_visibility else R.drawable.component_1_ic_visibility_off),
                        contentDescription = "Toggle Confirm Password Visibility"
                    )
                }
            },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(14.dp), // 모서리를 둥글게 설정
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color(0xFFF7F8F8), // 배경색 설정
                focusedBorderColor = Color.Transparent, // 포커스 시 테두리 제거
                unfocusedBorderColor = Color.Transparent // 비포커스 시 테두리 제거
            ),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None, // 자동 대문자 비활성화
                keyboardType = KeyboardType.Text,            // 일반 텍스트 키보드
                imeAction = ImeAction.Done                   // 완료 버튼 사용
            )

        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    if (password == confirmPassword) {
                        val success = performSignUpService(id = id, pw = password, context = context)
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
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), // 버튼의 기본 색을 투명으로 설정
            contentPadding = PaddingValues() // 내용물의 패딩을 없앰
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF92A3FD), // 첫 번째 색 (Hex)
                                Color(0xFF9DCEFF)  // 두 번째 색 (Hex)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center // 버튼 텍스트를 중앙 정렬
            ) {
                Text(text = "회원가입", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate("loginScreen")}) {
            Text("이미 계정이 있으신가요? 로그인", color = Color.Gray)
        }
    }
}




/*
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
        Text(
            modifier = Modifier.width(89.dp).height(30.dp),
            text = "회원가입",
            style = TextStyle(
                fontSize = 24.sp,
                lineHeight = 30.sp,
                fontFamily = FontFamily(Font(R.font.poppins)),
                fontWeight = FontWeight(700),
                color = Color(0xFF1D1617),
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        BasicTextField(
            value = inputID,
            onValueChange = { inputID = it },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp),
            modifier =  Modifier.border(width = 1.dp, color = Color(0xFFF7F8F8), shape = RoundedCornerShape(size = 14.dp))
            .width(315.dp)
            .height(48.dp)
            .background(color = Color(0xFFF7F8F8),
                shape = RoundedCornerShape(size = 14.dp)),
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

 */