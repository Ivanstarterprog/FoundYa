package com.example.foundya

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.foundya.ui.navigation.NavGraph
import com.example.foundya.ui.theme.FoundYaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoundYaTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}