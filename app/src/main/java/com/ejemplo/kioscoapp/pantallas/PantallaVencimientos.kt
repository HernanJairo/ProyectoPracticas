package com.ejemplo.kioscoapp.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
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

// Modelo de datos simulado para vencimientos
enum class EstadoVencimiento { VENCIDO, PROXIMO, OK }

data class ProductoVencimiento(
    val id: Int,
    val nombre: String,
    val lote: String,
    val fechaVencimiento: String,
    val stock: Int,
    val estado: EstadoVencimiento
)

// Lista de prueba (Mock Data)
val listaVencimientosPrueba = listOf(
    ProductoVencimiento(1, "Yogur Entero 200ml", "L-9042", "05/08/2026", 8, EstadoVencimiento.VENCIDO),
    ProductoVencimiento(2, "Sánguche de Miga", "L-1102", "09/08/2026", 12, EstadoVencimiento.PROXIMO),
    ProductoVencimiento(3, "AlfaAlfajor de Dulce de Leche", "L-8831", "12/08/2026", 20, EstadoVencimiento.PROXIMO),
    ProductoVencimiento(4, "Gaseosa Cola 500ml", "L-3310", "20/11/2026", 45, EstadoVencimiento.OK),
    ProductoVencimiento(5, "Galletitas Chocolate", "L-4491", "15/10/2026", 15, EstadoVencimiento.OK)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaVencimientos(navController: NavController) {
    var filtroSeleccionado by remember { mutableStateOf("Todos") }

    val listaFiltrada = when (filtroSeleccionado) {
        "Críticos" -> listaVencimientosPrueba.filter { it.estado == EstadoVencimiento.VENCIDO }
        "Próximos" -> listaVencimientosPrueba.filter { it.estado == EstadoVencimiento.PROXIMO }
        "En Regla" -> listaVencimientosPrueba.filter { it.estado == EstadoVencimiento.OK }
        else -> listaVencimientosPrueba
    }

    val totalVencidos = listaVencimientosPrueba.count { it.estado == EstadoVencimiento.VENCIDO }
    val totalProximos = listaVencimientosPrueba.count { it.estado == EstadoVencimiento.PROXIMO }
    val totalOk = listaVencimientosPrueba.count { it.estado == EstadoVencimiento.OK }

    Scaffold(
        topBar = {
            BarraSuperior(titulo = "Control de Vencimientos")
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
                .padding(16.dp)
        ) {
            // TARJETAS RESUMEN (KPIs)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TarjetaResumen(
                    titulo = "Vencidos",
                    cantidad = totalVencidos.toString(),
                    colorBorde = Color(0xFFFF5252),
                    modifier = Modifier.weight(1f)
                )
                TarjetaResumen(
                    titulo = "Próximos",
                    cantidad = totalProximos.toString(),
                    colorBorde = KioscoMostaza,
                    modifier = Modifier.weight(1f)
                )
                TarjetaResumen(
                    titulo = "En Regla",
                    cantidad = totalOk.toString(),
                    colorBorde = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // FILTROS HORIZONTALES
            Text(
                text = "Filtrar por estado:",
                color = KioscoDorado,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val opcionesFiltro = listOf("Todos", "Críticos", "Próximos", "En Regla")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(opcionesFiltro) { opcion ->
                    FilterChip(
                        selected = filtroSeleccionado == opcion,
                        onClick = { filtroSeleccionado = opcion },
                        label = { Text(opcion) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KioscoMostaza,
                            selectedLabelColor = KioscoNegro,
                            containerColor = KioscoSuperficie,
                            labelColor = Color.White
                        )
                    )
                }
            }

            // LISTA DE PRODUCTOS CON ALERTAS
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(listaFiltrada) { item ->
                    TarjetaItemVencimiento(item)
                }
            }
        }
    }
}

@Composable
fun TarjetaResumen(titulo: String, cantidad: String, colorBorde: Color, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = KioscoSuperficie),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = cantidad, color = colorBorde, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(text = titulo, color = Color.LightGray, fontSize = 12.sp)
        }
    }
}

@Composable
fun TarjetaItemVencimiento(item: ProductoVencimiento) {
    val (colorChip, textoChip) = when (item.estado) {
        EstadoVencimiento.VENCIDO -> Color(0xFFFF5252) to "VENCIDO"
        EstadoVencimiento.PROXIMO -> KioscoMostaza to "POR VENCER"
        EstadoVencimiento.OK -> Color(0xFF4CAF50) to "EN REGLA"
    }

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (item.estado == EstadoVencimiento.VENCIDO) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Alerta",
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = item.nombre,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Lote: ${item.lote} • Stock: ${item.stock} u.",
                    color = KioscoDorado,
                    fontSize = 13.sp
                )
                Text(
                    text = "Vence: ${item.fechaVencimiento}",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .background(colorChip.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = textoChip,
                    color = colorChip,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}