//package com.jeoktoma.drivemate
//
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.navigation.NavController
//import com.google.relay.compose.BoxScopeInstance.columnWeight
//import com.google.relay.compose.BoxScopeInstance.rowWeight
//import com.jeoktoma.drivemate.signup.SignUp
//
//
//
//
//@Composable
//fun SignUpScreen(navController: NavController) {
//    var inputName by remember { mutableStateOf("") }
//    var inputID by remember { mutableStateOf("") }
//    var inputPW by remember { mutableStateOf("") }
//    var checkPW by remember { mutableStateOf("") }
//    val context = LocalContext.current
//    val coroutineScope = rememberCoroutineScope()
//
//    SignUp(
//        alreadyUser = {navController.navigate("loginScreen")},
//        userName = "이름",
//        userPw = "비밀번호",
//        userConfirmPw = "비밀번호 확인",
//        userId = "아이디",
//        modifier = Modifier.rowWeight(1.0f).columnWeight(1.0f)
//    )
//
//    /*
//
//    SignUp(
//        registerButton = {
//            coroutineScope.launch {
//                if (inputPW == checkPW) {
//                    val success = performSignUpService(id = inputID, pw = inputPW, context = context)
//                    if (success) {
//                        Toast.makeText(context, "계정 생성 성공", Toast.LENGTH_SHORT).show()
//                        navController.popBackStack()
//                    } else {
//                        Toast.makeText(context, "계정 생성 실패", Toast.LENGTH_SHORT).show()
//                    }
//                } else {
//                    Toast.makeText(context, "비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
//                }
//            }
//        },
//        alreadyUser = {navController.navigate("loginScreen")},
//        userName = inputName,
//        userPw = inputPW,
//        userConfirmPw = checkPW,
//        userId = inputID,
//        modifier = Modifier.rowWeight(1.0f).columnWeight(1.0f)
//    )
//    * */
//
//}
//
//
//
///*
//@Composable
//fun SignUpScreen(navController: NavController) {
//    var inputID by remember { mutableStateOf("") }
//    var inputPW by remember { mutableStateOf("") }
//    var checkPW by remember { mutableStateOf("") }
//    val context = LocalContext.current
//    val coroutineScope = rememberCoroutineScope()
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text(
//            modifier = Modifier.width(89.dp).height(30.dp),
//            text = "회원가입",
//            style = TextStyle(
//                fontSize = 24.sp,
//                lineHeight = 30.sp,
//                fontFamily = FontFamily(Font(R.font.poppins)),
//                fontWeight = FontWeight(700),
//                color = Color(0xFF1D1617),
//            )
//        )
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        BasicTextField(
//            value = inputID,
//            onValueChange = { inputID = it },
////            modifier = Modifier
////                .fillMaxWidth()
////                .padding(8.dp),
//            modifier =  Modifier.border(width = 1.dp, color = Color(0xFFF7F8F8), shape = RoundedCornerShape(size = 14.dp))
//            .width(315.dp)
//            .height(48.dp)
//            .background(color = Color(0xFFF7F8F8),
//                shape = RoundedCornerShape(size = 14.dp)),
//            decorationBox = { innerTextField ->
//                Box(Modifier.padding(16.dp)) {
//                    if (inputID.isEmpty()) Text("아이디 입력")
//                    innerTextField()
//                }
//            }
//        )
//
//        BasicTextField(
//            value = inputPW,
//            onValueChange = { inputPW = it },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp),
//            decorationBox = { innerTextField ->
//                Box(Modifier.padding(16.dp)) {
//                    if (inputPW.isEmpty()) Text("비밀번호 입력")
//                    innerTextField()
//                }
//            }
//        )
//
//        BasicTextField(
//            value = checkPW,
//            onValueChange = { checkPW = it },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp),
//            decorationBox = { innerTextField ->
//                Box(Modifier.padding(16.dp)) {
//                    if (checkPW.isEmpty()) Text("비밀번호 확인")
//                    innerTextField()
//                }
//            }
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(onClick = {
//            CoroutineScope(Dispatchers.Main).launch {
//                if (inputPW == checkPW) {
//                    val success = performSignUpService(id = inputID, pw = inputPW, context = context)
//                    if (success) {
//                        Toast.makeText(context, "계정 생성 성공", Toast.LENGTH_SHORT).show()
//                        navController.popBackStack()
//                    } else {
//                        Toast.makeText(context, "계정 생성 실패", Toast.LENGTH_SHORT).show()
//                    }
//                } else {
//                    Toast.makeText(context, "비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }) {
//            Text("회원가입")
//        }
//
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Text(
//            text = "로그인 화면으로 돌아가기",
//            color = Color.Black,
//            fontSize = 14.sp,
//            modifier = Modifier.clickable {
//                navController.popBackStack()
//            }
//        )
//    }
//
//}
//
// */