package com.ejemplo.kioscoapp.componentes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ejemplo.kioscoapp.ui.theme.KioscoDorado
import com.ejemplo.kioscoapp.ui.theme.KioscoMostaza
import com.ejemplo.kioscoapp.ui.theme.KioscoSuperficie

@Composable
fun BarraInferior(navController: NavController, rol: String) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry.value?.destination?.route

    val esAdmin = rol == "admin" || rol == "administrador"

    NavigationBar(
        containerColor = KioscoSuperficie
    ) {
        // Catálogo - Vendedor y Admin
        NavigationBarItem(
            selected = rutaActual == "catalogo",
            onClick = {
                if (rutaActual != "catalogo") navController.navigate("catalogo")
            },
            label = { Text("Catálogo") },
            icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "Catálogo") },
            colors = navigationItemColors()
        )

        // Ventas - Vendedor y Admin
        NavigationBarItem(
            selected = rutaActual == "ventas",
            onClick = {
                if (rutaActual != "ventas") navController.navigate("ventas")
            },
            label = { Text("Ventas") },
            icon = { Icon(Icons.Default.PointOfSale, contentDescription = "Ventas") },
            colors = navigationItemColors()
        )

        // Gestión Alumnos - Vendedor y Admin
        NavigationBarItem(
            selected = rutaActual == "gestion_alumnos",
            onClick = {
                if (rutaActual != "gestion_alumnos") navController.navigate("gestion_alumnos")
            },
            label = { Text("Alumnos") },
            icon = { Icon(Icons.Default.People, contentDescription = "Alumnos") },
            colors = navigationItemColors()
        )

        // Alertas y Reportes - Solo Admin (se quitó el ícono redundante de "Usuarios",
        // esa gestión ahora se accede desde el menú del avatar en BarraSuperior)
        if (esAdmin) {
            NavigationBarItem(
                selected = rutaActual == "vencimientos",
                onClick = {
                    if (rutaActual != "vencimientos") navController.navigate("vencimientos")
                },
                label = { Text("Alertas") },
                icon = { Icon(Icons.Default.Warning, contentDescription = "Vencimientos") },
                colors = navigationItemColors()
            )

            NavigationBarItem(
                selected = rutaActual == "reportes",
                onClick = {
                    if (rutaActual != "reportes") navController.navigate("reportes")
                },
                label = { Text("Reportes") },
                icon = { Icon(Icons.Default.BarChart, contentDescription = "Reportes") },
                colors = navigationItemColors()
            )
        }
    }
}

@Composable
private fun navigationItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = KioscoMostaza,
    selectedTextColor = KioscoMostaza,
    unselectedIconColor = KioscoDorado.copy(alpha = 0.6f),
    unselectedTextColor = KioscoDorado.copy(alpha = 0.6f),
    indicatorColor = KioscoSuperficie
)