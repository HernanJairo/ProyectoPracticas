package com.ejemplo.kioscoapp.pantallas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ejemplo.kioscoapp.componentes.BarraInferior
import com.ejemplo.kioscoapp.componentes.BarraSuperior
import com.ejemplo.kioscoapp.ui.theme.KioscoDorado
import com.ejemplo.kioscoapp.ui.theme.KioscoMostaza
import com.ejemplo.kioscoapp.ui.theme.KioscoNegro
import com.ejemplo.kioscoapp.ui.theme.KioscoSuperficie
import com.ejemplo.kioscoapp.ui.theme.KioscoTextoSecundario
import java.text.NumberFormat
import java.util.Locale

data class Producto(
    val nombre: String,
    val categoria: String,
    val precio: Double,
    val stock: Int
)

// Catálogo de prueba. Cuando exista la colección "PRODUCTOS" en Firestore,
// esta lista se puede reemplazar por una carga con addSnapshotListener igual
// que se hace con CLIENTES en PantallaGestionAlumnos.
private val CATALOGO_PRODUCTOS = listOf(
    // Golosinas
    Producto("Alfajor Jorgito", "Golosinas", 800.0, 24),
    Producto("Chupetín Pico Dulce", "Golosinas", 300.0, 40),
    Producto("Caramelos Media Hora x5", "Golosinas", 400.0, 60),
    Producto("Chocolate 60g", "Golosinas", 1500.0, 15),

    // Bebidas
    Producto("Agua Mineral 500ml", "Bebidas", 900.0, 30),
    Producto("Gaseosa Cola 500ml", "Bebidas", 1300.0, 25),
    Producto("Jugo en caja 200ml", "Bebidas", 700.0, 35),
    Producto("Bebida Isotónica 500ml", "Bebidas", 1600.0, 12),

    // Galletas
    Producto("Galletitas Rellenas Chocolate", "Galletas", 1100.0, 20),
    Producto("Galletitas Rellenas Vainilla", "Galletas", 950.0, 18),
    Producto("Galletitas de Agua x6", "Galletas", 600.0, 22),

    // Snacks
    Producto("Papas Fritas 45g", "Snacks", 1200.0, 28),
    Producto("Palitos Salados 100g", "Snacks", 1000.0, 20),
    Producto("Maní Salado 100g", "Snacks", 850.0, 16),

    // Combos
    Producto("Combo Merienda (Galleta + Jugo)", "Combos", 1400.0, 10),
    Producto("Combo Recreo (Snack + Gaseosa)", "Combos", 2000.0, 8),
    Producto("Combo Golosinas x3", "Combos", 1800.0, 12)
)

private val ORDEN_CATEGORIAS = listOf("Golosinas", "Bebidas", "Galletas", "Snacks", "Combos")

@Composable
fun PantallaCatalogo(
    navController: NavController,
    esInvitado: Boolean = false,
    rol: String = "",
    nombreUsuario: String = ""
) {
    val productosPorCategoria = remember {
        ORDEN_CATEGORIAS.associateWith { categoria ->
            CATALOGO_PRODUCTOS.filter { it.categoria == categoria }
        }
    }

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "Catálogo de Productos",
                // Invitado: solo flecha para volver al login. Logueado: sin flecha, con avatar.
                mostrarBotonAtras = esInvitado,
                navController = navController,
                nombreUsuario = nombreUsuario,
                rolUsuario = rol,
                esInvitado = esInvitado
            )
        },
        bottomBar = {
            // Invitado: se oculta la BarraInferior por completo
            if (!esInvitado) {
                BarraInferior(navController = navController, rol = rol)
            }
        },
        containerColor = KioscoNegro
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ORDEN_CATEGORIAS.forEach { categoria ->
                val productos = productosPorCategoria[categoria].orEmpty()
                if (productos.isNotEmpty()) {
                    item(key = "header_$categoria") {
                        Text(
                            text = categoria,
                            color = KioscoMostaza,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    items(productos, key = { it.nombre }) { producto ->
                        TarjetaProducto(producto)
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaProducto(producto: Producto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = KioscoSuperficie),
        border = BorderStroke(1.dp, KioscoDorado.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = producto.nombre,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = KioscoTextoSecundario,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Stock: ${producto.stock}",
                        color = KioscoTextoSecundario,
                        fontSize = 12.sp
                    )
                }
            }
            Text(
                text = formatearPrecio(producto.precio),
                color = KioscoMostaza,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
        }
    }
}

private fun formatearPrecio(precio: Double): String {
    val formato = NumberFormat.getCurrencyInstance(Locale("es", "AR"))
    return formato.format(precio)
}