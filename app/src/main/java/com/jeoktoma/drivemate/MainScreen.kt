package com.jeoktoma.drivemate

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

//길찾기(path), 운행(drive), 설문(survey), 리마인드(tip), 사용자 프로필(이름, 칭호, 레벨 등 유저 정보)
@Composable
fun MainScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(16.dp)
                .background(Color.Gray)
                .clickable { navController.navigate("pathScreen") },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "길 찾기")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp)
                    .padding(16.dp)
                    .background(Color.Gray)
                    .clickable { navController.navigate("driveScreen") },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "운행 리포트")
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp)
                    .padding(16.dp)
                    .background(Color.Gray)
                    .clickable { navController.navigate("surveyScreen") },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "설문")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp)
                    .padding(16.dp)
                    .background(Color.Gray)
                    .clickable { navController.navigate("tipScreen") },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "팁")
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp)
                    .padding(16.dp)
                    .background(Color.Gray)
                    .clickable { /*TODO*/ },
                contentAlignment = Alignment.Center

            ) {
                Text(text = "사용자 프로필")
            }
        }
    }
}

