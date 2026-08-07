package com.ejemplo.kioscoapp.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ejemplo.kioscoapp.pantallas.PantallaCatalogo
import com.ejemplo.kioscoapp.pantallas.PantallaLogin
import com.ejemplo.kioscoapp.pantallas.PantallaProductos
import com.ejemplo.kioscoapp.pantallas.PantallaReportes
import com.ejemplo.kioscoapp.pantallas.PantallaVencimientos
import com.ejemplo.kioscoapp.pantallas.PantallaVentas

@Composable
fun NavegacionApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            PantallaLogin(
                onIrACatalogo = { navController.navigate("catalogo") },
                onIngresoAlumno = { _ -> navController.navigate("catalogo") },
                onIniciarSesion = { _, _, _ -> navController.navigate("ventas") }
            )
        }
        composable("catalogo") { PantallaCatalogo(navController) }
        composable("productos") { PantallaProductos(navController) }
        composable("vencimientos") { PantallaVencimientos(navController) }
        composable("ventas") { PantallaVentas(navController) }
        composable("reportes") { PantallaReportes(navController) }
    }
}