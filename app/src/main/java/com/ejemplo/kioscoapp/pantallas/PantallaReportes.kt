package com.ejemplo.kioscoapp.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

val rankingMasVendidos = listOf(
    "1. Coca Cola 500ml" to "120 u.",
    "2. Sánguche de Miga" to "85 u.",
    "3. Alfajor Jorrat" to "74 u.",
    "4. Agua Mineral" to "50 u.",
    "5. Galletitas Chocolate" to "38 u."
)

@Composable
fun PantallaReportes(navController: NavController) {
    Scaffold(
        topBar = { BarraSuperior(titulo = "Reportes y Métricas") },
        bottomBar = { BarraInferior(navController = navController) },
        containerColor = KioscoNegro
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TarjetaKpi(
                    titulo = "Ventas Hoy",
                    monto = "$45.200",
                    modifier = Modifier.weight(1f)
                )
                TarjetaKpi(
                    titulo = "Ganancia",
                    monto = "$18.500",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Top 5 Más Vendidos",
                color = KioscoMostaza,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(rankingMasVendidos) { _, (item, cantidad) ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = KioscoSuperficie),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = item, color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text(text = cantidad, color = KioscoMostaza, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaKpi(titulo: String, monto: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = KioscoSuperficie),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = monto, color = KioscoMostaza, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(text = titulo, color = KioscoDorado, fontSize = 12.sp)
        }
    }
}