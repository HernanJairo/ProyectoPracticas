package com.ejemplo.kioscoapp.pantallas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ejemplo.kioscoapp.componentes.BarraInferior
import com.ejemplo.kioscoapp.componentes.BarraSuperior
import com.ejemplo.kioscoapp.ui.theme.KioscoDorado
import com.ejemplo.kioscoapp.ui.theme.KioscoMostaza
import com.ejemplo.kioscoapp.ui.theme.KioscoNegro
import com.ejemplo.kioscoapp.ui.theme.KioscoOnMostaza
import com.ejemplo.kioscoapp.ui.theme.KioscoSuperficie
import com.ejemplo.kioscoapp.ui.theme.KioscoTextoSecundario
import java.text.NumberFormat
import java.util.Locale

data class Producto(
    val id: Int,
    val nombre: String,
    val precio: Double,
    val categoria: String,
    val stock: Int
)

private val categorias = listOf(
    "Todos",
    "Bebidas",
    "Golosinas",
    "Galletas",
    "Snacks",
    "Combos"
)

private val productosMock = listOf(
    Producto(1, "Coca Cola 500 ml", 1500.0, "Bebidas", 24),
    Producto(2, "Agua Mineral 500 ml", 1000.0, "Bebidas", 30),
    Producto(3, "Jugo Brik Multifruta", 750.0, "Bebidas", 20),
    Producto(4, "Alfajor Jorgito Triple", 800.0, "Golosinas", 15),
    Producto(5, "Chupetín Flynn Paff", 500.0, "Golosinas", 40),
    Producto(6, "Oreo Original 118 g", 1200.0, "Galletas", 18),
    Producto(7, "Pepitos Clásicas", 900.0, "Galletas", 22),
    Producto(8, "Papas Lays Clásicas", 1800.0, "Snacks", 12),
    Producto(9, "Combo Almuerzo", 3500.0, "Combos", 8),
    Producto(10, "Combo Merienda", 2200.0, "Combos", 10)
)

private fun formatearPrecio(precio: Double): String {
    val formato = NumberFormat.getNumberInstance(Locale("es", "AR"))
    formato.minimumFractionDigits = 0
    formato.maximumFractionDigits = 0
    return "$${formato.format(precio)}"
}

@Composable
fun PantallaCatalogo(navController: NavController) {
    var textoBusqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Todos") }

    val productosFiltrados = remember(textoBusqueda, categoriaSeleccionada) {
        productosMock.filter { producto ->
            val coincideBusqueda = producto.nombre.contains(textoBusqueda.trim(), ignoreCase = true)
            val coincideCategoria = categoriaSeleccionada == "Todos" ||
                producto.categoria == categoriaSeleccionada
            coincideBusqueda && coincideCategoria
        }
    }

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "Catálogo de Productos",
                mostrarBotonAtras = true,
                navController = navController
            )
        },
        bottomBar = {
            BarraInferior(navController = navController)
        },
        containerColor = KioscoNegro
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Buscar productos...",
                        color = KioscoTextoSecundario
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = KioscoMostaza
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = KioscoMostaza,
                    unfocusedBorderColor = KioscoDorado.copy(alpha = 0.5f),
                    focusedLabelColor = KioscoMostaza,
                    cursorColor = KioscoMostaza,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = KioscoSuperficie,
                    unfocusedContainerColor = KioscoSuperficie
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(end = 4.dp)
            ) {
                items(categorias) { categoria ->
                    FilterChip(
                        selected = categoriaSeleccionada == categoria,
                        onClick = { categoriaSeleccionada = categoria },
                        label = {
                            Text(
                                text = categoria,
                                fontWeight = if (categoriaSeleccionada == categoria) {
                                    FontWeight.SemiBold
                                } else {
                                    FontWeight.Normal
                                }
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = KioscoSuperficie,
                            labelColor = KioscoDorado,
                            selectedContainerColor = KioscoMostaza,
                            selectedLabelColor = KioscoOnMostaza
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = categoriaSeleccionada == categoria,
                            borderColor = KioscoDorado.copy(alpha = 0.4f),
                            selectedBorderColor = KioscoMostaza
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${productosFiltrados.size} producto(s) encontrado(s)",
                color = KioscoTextoSecundario,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (productosFiltrados.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 48.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No se encontraron productos",
                        color = KioscoDorado,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Text(
                        text = "Probá con otra búsqueda o categoría",
                        color = KioscoTextoSecundario,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(productosFiltrados, key = { it.id }) { producto ->
                        TarjetaProducto(
                            producto = producto,
                            onAgregar = { /* Próximamente: agregar al carrito */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaProducto(
    producto: Producto,
    onAgregar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = KioscoSuperficie),
        border = BorderStroke(1.dp, KioscoDorado.copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = KioscoDorado.copy(alpha = 0.15f)
            ) {
                Text(
                    text = producto.categoria,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = KioscoDorado,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = producto.nombre,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Text(
                text = formatearPrecio(producto.precio),
                color = KioscoMostaza,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Text(
                text = "Stock: ${producto.stock} u.",
                color = if (producto.stock <= 10) {
                    KioscoDorado
                } else {
                    KioscoTextoSecundario
                },
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onAgregar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = KioscoMostaza,
                    contentColor = KioscoOnMostaza
                ),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = "Agregar",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
