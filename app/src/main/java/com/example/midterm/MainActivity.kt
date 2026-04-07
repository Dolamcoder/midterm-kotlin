package com.example.midterm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.midterm.navigation.AppNavigation
import com.example.midterm.ui.theme.MidtermTheme
import com.example.midterm.util.ImageUploadService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Khởi tạo Image Upload Service
        ImageUploadService.init(this)
        
        setContent {
            MidtermTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}