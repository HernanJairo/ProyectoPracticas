package com.ejemplo.kioscoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ejemplo.kioscoapp.navegacion.NavegacionApp
import com.ejemplo.kioscoapp.ui.theme.AppKioscoTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }
        enableEdgeToEdge()
        setContent {
            AppKioscoTheme {
                NavegacionApp()
            }
        }
    }
}