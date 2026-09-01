package com.ejemplo.kioscoapp.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
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

data class ProductoInventario(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val precioCosto: String,
    val precioVenta: String,
    val stock: Int
)

val listaInventarioPrueba = mutableStateListOf(
    ProductoInventario(1, "Alfajor Jorrat", "Golosinas", "$400", "$800", 35),
    ProductoInventario(2, "Coca Cola 500ml", "Bebidas", "$900", "$1.500", 20),
    ProductoInventario(3, "Sánguche de Miga", "Comida", "$1.200", "$2.200", 10),
    ProductoInventario(4, "Agua Mineral 500ml", "Bebidas", "$500", "$1.000", 50)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaProductos(navController: NavController, rol: String, nombreUsuario: String) {
    var busqueda by remember { mutableStateOf("") }
    var mostrarDialogoAgregar by remember { mutableStateOf(false) }

    val productosFiltrados = listaInventarioPrueba.filter {
        it.nombre.contains(busqueda, ignoreCase = true) || it.categoria.contains(busqueda, ignoreCase = true)
    }

    Scaffold(
        topBar = { 
            BarraSuperior(
                titulo = "Gestión de Inventario",
                nombreUsuario = nombreUsuario,
                rolUsuario = rol,
                navController = navController
            ) 
        },
        bottomBar = { BarraInferior(navController = navController, rol = rol) },
        floatingActionButton = {
            if (rol == "admin" || rol == "administrador") {
                FloatingActionButton(
                    onClick = { mostrarDialogoAgregar = true },
                    containerColor = KioscoMostaza,
                    contentColor = KioscoNegro
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar producto")
                }
            }
        },
        containerColor = KioscoNegro
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                label = { Text("Buscar en inventario...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = KioscoMostaza) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = KioscoMostaza,
                    unfocusedBorderColor = KioscoDorado,
                    focusedLabelColor = KioscoMostaza,
                    unfocusedLabelColor = KioscoDorado
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Total de artículos: ${productosFiltrados.size}",
                color = KioscoDorado,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(productosFiltrados) { prod ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = KioscoSuperficie),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(14.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = prod.nombre, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(text = "Cat: ${prod.categoria} • Stock: ${prod.stock} u.", color = KioscoDorado, fontSize = 13.sp)
                                Text(text = "Costo: ${prod.precioCosto} | Venta: ${prod.precioVenta}", color = KioscoMostaza, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }
                            Row {
                                IconButton(onClick = { /* Editar */ }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = KioscoDorado)
                                }
                                IconButton(onClick = { listaInventarioPrueba.remove(prod) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFFF5252))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogoAgregar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoAgregar = false },
            containerColor = KioscoSuperficie,
            title = { Text("Nuevo Producto", color = KioscoMostaza) },
            text = { Text("Aquí irá el formulario para registrar costo, precio y stock inicial.") },
            confirmButton = {
                Button(
                    onClick = { mostrarDialogoAgregar = false },
                    colors = ButtonDefaults.buttonColors(containerColor = KioscoMostaza, contentColor = KioscoNegro)
                ) { Text("Entendido") }
            }
        )
    }
}