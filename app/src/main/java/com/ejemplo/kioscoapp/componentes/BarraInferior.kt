package com.ejemplo.kioscoapp.componentes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ejemplo.kioscoapp.ui.theme.KioscoDorado
import com.ejemplo.kioscoapp.ui.theme.KioscoMostaza
import com.ejemplo.kioscoapp.ui.theme.KioscoSuperficie

@Composable
fun BarraInferior(navController: NavController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry.value?.destination?.route

    NavigationBar(
        containerColor = KioscoSuperficie
    ) {
        NavigationBarItem(
            selected = rutaActual == "catalogo",
            onClick = { navController.navigate("catalogo") },
            label = { Text("Catálogo") },
            icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "Catálogo") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = KioscoMostaza,
                selectedTextColor = KioscoMostaza,
                unselectedIconColor = KioscoDorado.copy(alpha = 0.6f),
                unselectedTextColor = KioscoDorado.copy(alpha = 0.6f)
            )
        )
        NavigationBarItem(
            selected = rutaActual == "ventas",
            onClick = { navController.navigate("ventas") },
            label = { Text("Ventas") },
            icon = { Icon(Icons.Default.PointOfSale, contentDescription = "Ventas") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = KioscoMostaza,
                selectedTextColor = KioscoMostaza,
                unselectedIconColor = KioscoDorado.copy(alpha = 0.6f),
                unselectedTextColor = KioscoDorado.copy(alpha = 0.6f)
            )
        )
        NavigationBarItem(
            selected = rutaActual == "vencimientos",
            onClick = { navController.navigate("vencimientos") },
            label = { Text("Alertas") },
            icon = { Icon(Icons.Default.Warning, contentDescription = "Vencimientos") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = KioscoMostaza,
                selectedTextColor = KioscoMostaza,
                unselectedIconColor = KioscoDorado.copy(alpha = 0.6f),
                unselectedTextColor = KioscoDorado.copy(alpha = 0.6f)
            )
        )
        NavigationBarItem(
            selected = rutaActual == "reportes",
            onClick = { navController.navigate("reportes") },
            label = { Text("Reportes") },
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Reportes") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = KioscoMostaza,
                selectedTextColor = KioscoMostaza,
                unselectedIconColor = KioscoDorado.copy(alpha = 0.6f),
                unselectedTextColor = KioscoDorado.copy(alpha = 0.6f)
            )
        )
    }
}