package com.ejemplo.kioscoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ejemplo.kioscoapp.navegacion.NavegacionApp
import com.ejemplo.kioscoapp.ui.theme.AppKioscoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppKioscoTheme {
                NavegacionApp()
            }
        }
    }
}