package com.jeoktoma.drivemate

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

//길찾기(path), 운행(drive), 설문(survey), 리마인드(tip), 사용자 프로필(이름, 칭호, 레벨 등 유저 정보)
//@Composable
//fun MainScreen(navController: NavHostController) {
//    com.jeoktoma.drivemate.mainscreen.MainScreen(
//        searchTap = {
//                    navController.navigate("pathScreen")
//        },
//        homeActiveTap = {},
//        profileTap = {},
//        reportTap = {},
//        tipTap = {},
//        reportDateText = "2024-11-11",
//        viewMoreButton = {
//            navController.navigate("tipScreen")
//        },
//        achievementTitleText = "코너링이 훌륭하시네요,",
//        userNameText = "Gildong Hong",
//        modifier = Modifier.rowWeight(1.0f).columnWeight(1.0f)
//    )
//}

/*
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
 */
