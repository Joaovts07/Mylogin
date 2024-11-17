package com.example.mylogin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.mylogin.ui.LoginScreen
import com.example.mylogin.ui.theme.MyLoginTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {       
            MyLoginTheme {
                LoginScreen()
            }
        }
    }
}    

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyLoginTheme {
        LoginScreen()
    }
}