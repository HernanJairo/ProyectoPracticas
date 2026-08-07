package com.ejemplo.kioscoapp.pantallas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.ejemplo.kioscoapp.ui.theme.KioscoSuperficieVariante
import com.ejemplo.kioscoapp.ui.theme.KioscoTextoSecundario
import java.text.NumberFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private data class ProductoVenta(
    val codigo: String,
    val nombre: String,
    val precio: Double
)

private data class ItemCarrito(
    val producto: ProductoVenta,
    val cantidad: Int
)

private enum class MedioPago(val etiqueta: String) {
    EFECTIVO("Efectivo"),
    MERCADO_PAGO("Mercado Pago"),
    DEBITO("Débito")
}

private data class VentaRegistrada(
    val id: Int,
    val hora: String,
    val medioPago: MedioPago,
    val total: Double,
    val cantidadItems: Int
)

private val catalogoVentas = listOf(
    ProductoVenta("BEB001", "Coca Cola 500 ml", 1500.0),
    ProductoVenta("BEB002", "Agua Mineral 500 ml", 1000.0),
    ProductoVenta("BEB003", "Jugo Brik Multifruta", 750.0),
    ProductoVenta("GOL001", "Alfajor Jorgito Triple", 800.0),
    ProductoVenta("GOL002", "Chupetín Flynn Paff", 500.0),
    ProductoVenta("GAL001", "Oreo Original 118 g", 1200.0),
    ProductoVenta("GAL002", "Pepitos Clásicas", 900.0),
    ProductoVenta("SNK001", "Papas Lays Clásicas", 1800.0),
    ProductoVenta("CMB001", "Combo Almuerzo", 3500.0),
    ProductoVenta("CMB002", "Combo Merienda", 2200.0)
)

private val historialInicial = listOf(
    VentaRegistrada(1, "08:15", MedioPago.EFECTIVO, 2300.0, 2),
    VentaRegistrada(2, "09:42", MedioPago.MERCADO_PAGO, 1500.0, 1),
    VentaRegistrada(3, "10:05", MedioPago.DEBITO, 4200.0, 3),
    VentaRegistrada(4, "11:30", MedioPago.EFECTIVO, 800.0, 1),
    VentaRegistrada(5, "12:18", MedioPago.MERCADO_PAGO, 5700.0, 4)
)

private fun formatearPrecio(precio: Double): String {
    val formato = NumberFormat.getNumberInstance(Locale("es", "AR"))
    formato.minimumFractionDigits = 0
    formato.maximumFractionDigits = 0
    return "$${formato.format(precio)}"
}

private fun horaActual(): String {
    return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaVentas(navController: NavController) {
    var pestañaSeleccionada by remember { mutableIntStateOf(0) }
    val pestañas = listOf("Nueva Venta", "Historial de Hoy")
    val historialHoy = remember { mutableStateListOf(*historialInicial.toTypedArray()) }
    var contadorVentas by remember { mutableIntStateOf(historialInicial.size + 1) }

    Scaffold(
        topBar = {
            BarraSuperior(titulo = "Registro de Ventas")
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
        ) {
            TabRow(
                selectedTabIndex = pestañaSeleccionada,
                containerColor = KioscoSuperficie,
                contentColor = KioscoMostaza,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pestañaSeleccionada]),
                        color = KioscoMostaza,
                        height = 3.dp
                    )
                },
                divider = {
                    HorizontalDivider(color = KioscoDorado.copy(alpha = 0.2f))
                }
            ) {
                pestañas.forEachIndexed { index, titulo ->
                    Tab(
                        selected = pestañaSeleccionada == index,
                        onClick = { pestañaSeleccionada = index },
                        text = {
                            Text(
                                text = titulo,
                                fontWeight = if (pestañaSeleccionada == index) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Normal
                                },
                                color = if (pestañaSeleccionada == index) {
                                    KioscoMostaza
                                } else {
                                    KioscoDorado.copy(alpha = 0.7f)
                                }
                            )
                        }
                    )
                }
            }

            when (pestañaSeleccionada) {
                0 -> SeccionNuevaVenta(
                    historialHoy = historialHoy,
                    contadorVentas = contadorVentas,
                    onVentaRegistrada = { contadorVentas += 1 }
                )
                1 -> SeccionHistorialHoy(historial = historialHoy)
            }
        }
    }
}

@Composable
private fun SeccionNuevaVenta(
    historialHoy: MutableList<VentaRegistrada>,
    contadorVentas: Int,
    onVentaRegistrada: () -> Unit
) {
    var textoBusqueda by remember { mutableStateOf("") }
    val carrito = remember { mutableStateListOf<ItemCarrito>() }
    var mostrarDialogoCobro by remember { mutableStateOf(false) }
    var medioPagoSeleccionado by remember { mutableStateOf(MedioPago.EFECTIVO) }

    val productosCoincidentes = remember(textoBusqueda) {
        val consulta = textoBusqueda.trim()
        if (consulta.isEmpty()) {
            emptyList()
        } else {
            catalogoVentas.filter { producto ->
                producto.nombre.contains(consulta, ignoreCase = true) ||
                    producto.codigo.contains(consulta, ignoreCase = true)
            }
        }
    }

    val total = carrito.sumOf { it.producto.precio * it.cantidad }

    fun agregarAlCarrito(producto: ProductoVenta) {
        val indice = carrito.indexOfFirst { it.producto.codigo == producto.codigo }
        if (indice >= 0) {
            val item = carrito[indice]
            carrito[indice] = item.copy(cantidad = item.cantidad + 1)
        } else {
            carrito.add(ItemCarrito(producto, 1))
        }
        textoBusqueda = ""
    }

    fun modificarCantidad(codigo: String, delta: Int) {
        val indice = carrito.indexOfFirst { it.producto.codigo == codigo }
        if (indice < 0) return
        val nuevaCantidad = carrito[indice].cantidad + delta
        if (nuevaCantidad <= 0) {
            carrito.removeAt(indice)
        } else {
            carrito[indice] = carrito[indice].copy(cantidad = nuevaCantidad)
        }
    }

    fun eliminarItem(codigo: String) {
        carrito.removeAll { it.producto.codigo == codigo }
    }

    fun confirmarCobro() {
        if (carrito.isEmpty()) return
        val cantidadItems = carrito.sumOf { it.cantidad }
        historialHoy.add(
            0,
            VentaRegistrada(
                id = contadorVentas,
                hora = horaActual(),
                medioPago = medioPagoSeleccionado,
                total = total,
                cantidadItems = cantidadItems
            )
        )
        onVentaRegistrada()
        carrito.clear()
        mostrarDialogoCobro = false
        medioPagoSeleccionado = MedioPago.EFECTIVO
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = { textoBusqueda = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Buscar por código o nombre...", color = KioscoTextoSecundario)
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = KioscoMostaza)
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = coloresCampoVentas()
        )

        if (productosCoincidentes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = KioscoSuperficieVariante),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, KioscoDorado.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    productosCoincidentes.take(5).forEach { producto ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { agregarAlCarrito(producto) }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = producto.nombre,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = producto.codigo,
                                    color = KioscoTextoSecundario,
                                    fontSize = 12.sp
                                )
                            }
                            Text(
                                text = formatearPrecio(producto.precio),
                                color = KioscoMostaza,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Agregar",
                                tint = KioscoMostaza,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Cuenta actual (${carrito.sumOf { it.cantidad }} ítems)",
            color = KioscoDorado,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (carrito.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(containerColor = KioscoSuperficie),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, KioscoDorado.copy(alpha = 0.25f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = null,
                        tint = KioscoDorado.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Sin productos en la cuenta",
                        color = KioscoTextoSecundario,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Buscá un producto para comenzar",
                        color = KioscoTextoSecundario.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(carrito, key = { it.producto.codigo }) { item ->
                    TarjetaItemCarrito(
                        item = item,
                        onIncrementar = { modificarCantidad(item.producto.codigo, 1) },
                        onDecrementar = { modificarCantidad(item.producto.codigo, -1) },
                        onEliminar = { eliminarItem(item.producto.codigo) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = KioscoSuperficie,
            border = BorderStroke(1.dp, KioscoDorado.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total a pagar",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                Text(
                    text = formatearPrecio(total),
                    color = KioscoMostaza,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { if (carrito.isNotEmpty()) mostrarDialogoCobro = true },
            enabled = carrito.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = KioscoMostaza,
                contentColor = KioscoOnMostaza,
                disabledContainerColor = KioscoMostaza.copy(alpha = 0.3f),
                disabledContentColor = KioscoOnMostaza.copy(alpha = 0.5f)
            )
        ) {
            Icon(Icons.Default.Payments, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Cobrar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }

    if (mostrarDialogoCobro) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCobro = false },
            containerColor = KioscoSuperficieVariante,
            title = {
                Text(
                    text = "Seleccionar medio de pago",
                    color = KioscoMostaza,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Total: ${formatearPrecio(total)}",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    MedioPago.entries.forEach { medio ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { medioPagoSeleccionado = medio }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = medioPagoSeleccionado == medio,
                                onClick = { medioPagoSeleccionado = medio },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = KioscoMostaza,
                                    unselectedColor = KioscoDorado
                                )
                            )
                            Text(
                                text = medio.etiqueta,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { confirmarCobro() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KioscoMostaza,
                        contentColor = KioscoOnMostaza
                    )
                ) {
                    Text("Confirmar cobro")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoCobro = false }) {
                    Text("Cancelar", color = KioscoDorado)
                }
            }
        )
    }
}

@Composable
private fun TarjetaItemCarrito(
    item: ItemCarrito,
    onIncrementar: () -> Unit,
    onDecrementar: () -> Unit,
    onEliminar: () -> Unit
) {
    val subtotal = item.producto.precio * item.cantidad

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = KioscoSuperficie),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, KioscoDorado.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.producto.nombre,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${formatearPrecio(item.producto.precio)} c/u · ${item.producto.codigo}",
                    color = KioscoTextoSecundario,
                    fontSize = 12.sp
                )
                Text(
                    text = "Subtotal: ${formatearPrecio(subtotal)}",
                    color = KioscoMostaza,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onDecrementar,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = "Disminuir",
                        tint = KioscoDorado
                    )
                }
                Text(
                    text = item.cantidad.toString(),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                IconButton(
                    onClick = onIncrementar,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Aumentar",
                        tint = KioscoMostaza
                    )
                }
                IconButton(onClick = onEliminar) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SeccionHistorialHoy(historial: List<VentaRegistrada>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Ventas del día",
            color = KioscoDorado,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val totalDia = historial.sumOf { it.total }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            color = KioscoSuperficie,
            border = BorderStroke(1.dp, KioscoMostaza.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Recaudación de hoy",
                        color = KioscoTextoSecundario,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "${historial.size} ventas",
                        color = KioscoDorado,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = formatearPrecio(totalDia),
                    color = KioscoMostaza,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(historial.sortedByDescending { it.id }) { venta ->
                TarjetaVentaHistorial(venta = venta)
            }
        }
    }
}

@Composable
private fun TarjetaVentaHistorial(venta: VentaRegistrada) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = KioscoSuperficie),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, KioscoDorado.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Venta #${venta.id}",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${venta.hora} hs · ${venta.cantidadItems} ítem(s)",
                    color = KioscoTextoSecundario,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = KioscoDorado.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = venta.medioPago.etiqueta,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = KioscoDorado,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Text(
                text = formatearPrecio(venta.total),
                color = KioscoMostaza,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
private fun coloresCampoVentas() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = KioscoMostaza,
    unfocusedBorderColor = KioscoDorado.copy(alpha = 0.5f),
    cursorColor = KioscoMostaza,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    focusedContainerColor = KioscoSuperficie,
    unfocusedContainerColor = KioscoSuperficie
)
