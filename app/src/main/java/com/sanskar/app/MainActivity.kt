package com.sanskar.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sanskar.app.ui.SanskarApp
import com.sanskar.app.ui.theme.SanskarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SanskarTheme {
                SanskarApp()
            }
        }
    }
}
