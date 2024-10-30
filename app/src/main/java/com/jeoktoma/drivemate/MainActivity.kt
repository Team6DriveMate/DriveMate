package com.jeoktoma.drivemate

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jeoktoma.drivemate.ui.theme.DriveMateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DriveMateTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Login()
                }
            }
        }
    }
}


@Composable
fun Login(){
    var inputID by remember {mutableStateOf("")}
    var inputPW by remember {mutableStateOf("")}

    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally){
        Text("로그인",
            style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(128.dp))
        Text("아이디")
        OutlinedTextField(
            value = inputID,
            onValueChange = {inputID = it},
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("패스워드")
        OutlinedTextField(
            value = inputPW,
            onValueChange = {inputPW = it},
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row {
            val context = LocalContext.current
            Button(onClick = {
                performLogin(inputID, inputPW, context)
            })
            {
                Text("로그인")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    Login()
}