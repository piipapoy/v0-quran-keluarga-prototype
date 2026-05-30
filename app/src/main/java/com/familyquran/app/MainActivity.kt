package com.familyquran.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.familyquran.app.core.theme.RainaraQuranTheme
import com.familyquran.app.core.theme.QuranThemeColors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RainaraQuranTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = QuranThemeColors.ivory
                ) {
                    RainaraQuranApp()
                }
            }
        }
    }
}
