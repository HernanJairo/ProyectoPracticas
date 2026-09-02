package com.ejemplo.kioscoapp.pantallas

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.text.NumberFormat
import java.util.Locale

data class Alumno(
    val dni: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val saldo: Double = 0.0
) {
    val nombreCompleto: String
        get() = if (apellido.isNotBlank()) "$nombre $apellido" else nombre
}

data class ItemMovimientoAlumno(
    val detalle: String,
    val fecha: String,
    val monto: String
)

@Composable
fun PantallaGestionAlumnos(navController: NavController, rol: String, nombreUsuario: String) {
    val context = LocalContext.current
    val db = remember { FirebaseFirestore.getInstance() }

    var textoBusqueda by remember { mutableStateOf("") }
    val alumnos = remember { mutableStateListOf<Alumno>() }
    var cargando by remember { mutableStateOf(true) }

    var alumnoSeleccionado by remember { mutableStateOf<Alumno?>(null) }
    var mostrarDialogoDetalle by remember { mutableStateOf(false) }
    var mostrarDialogoCarga by remember { mutableStateOf(false) }
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var mostrarDialogoNuevoAlumno by remember { mutableStateOf(false) }
    var montoACargar by remember { mutableStateOf("") }

    val movimientosSimulados = remember {
        listOf(
            ItemMovimientoAlumno("1x Sánguche de Miga + 1x Coca Cola", "Hoy, 10:15 hs", "-$3.700"),
            ItemMovimientoAlumno("2x Alfajor Jorrat", "28/08, 16:30 hs", "-$1.600"),
            ItemMovimientoAlumno("Carga de Saldo (Efectivo en Caja)", "27/08, 08:20 hs", "+$5.000")
        )
    }

    // Carga en tiempo real de la colección CLIENTES
    DisposableEffect(Unit) {
        val registro: ListenerRegistration = db.collection("CLIENTES")
            .addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    alumnos.clear()
                    for (doc in snapshot.documents) {
                        alumnos.add(
                            Alumno(
                                dni = doc.id,
                                nombre = doc.getString("nombre") ?: "",
                                apellido = doc.getString("apellido") ?: "",
                                saldo = (doc.get("saldo") as? Number)?.toDouble() ?: 0.0
                            )
                        )
                    }
                }
                cargando = false
            }
        onDispose { registro.remove() }
    }

    val alumnosFiltrados = remember(textoBusqueda, alumnos.size) {
        if (textoBusqueda.isBlank()) {
            alumnos.toList()
        } else {
            val texto = textoBusqueda.trim()
            alumnos.filter {
                it.dni.contains(texto, ignoreCase = true) ||
                        it.nombre.contains(texto, ignoreCase = true) ||
                        it.apellido.contains(texto, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "Gestión de Alumnos",
                nombreUsuario = nombreUsuario,
                rolUsuario = rol,
                navController = navController
            )
        },
        bottomBar = { BarraInferior(navController = navController, rol = rol) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogoNuevoAlumno = true },
                containerColor = KioscoMostaza,
                contentColor = KioscoNegro
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Alumno")
            }
        },
        containerColor = KioscoNegro
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Buscador con filtrado en tiempo real
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                label = { Text("Buscar por DNI, Nombre o Apellido") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = KioscoMostaza) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = outlinedTextFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                cargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = KioscoMostaza)
                    }
                }
                alumnosFiltrados.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.PersonSearch,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = KioscoDorado.copy(alpha = 0.3f)
                            )
                            Text("No se encontraron alumnos", color = KioscoTextoSecundario)
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(alumnosFiltrados, key = { it.dni }) { alumno ->
                            TarjetaGestionAlumno(
                                alumno = alumno,
                                onClickTarjeta = {
                                    alumnoSeleccionado = alumno
                                    mostrarDialogoDetalle = true
                                },
                                onCargarSaldo = {
                                    alumnoSeleccionado = alumno
                                    montoACargar = ""
                                    mostrarDialogoCarga = true
                                },
                                onEliminar = {
                                    alumnoSeleccionado = alumno
                                    mostrarDialogoEliminar = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal de Detalle Completo del Alumno (al hacer click en la tarjeta)
    if (mostrarDialogoDetalle && alumnoSeleccionado != null) {
        val alumnoActual = alumnoSeleccionado!!
        AlertDialog(
            onDismissRequest = { mostrarDialogoDetalle = false },
            containerColor = KioscoSuperficie,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = KioscoMostaza, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(alumnoActual.nombreCompleto, color = KioscoMostaza, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = KioscoNegro),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("DNI: ${alumnoActual.dni}", color = Color.LightGray, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Saldo Disponible:", color = KioscoDorado, fontSize = 12.sp)
                            Text(
                                text = formatearPrecio(alumnoActual.saldo),
                                color = KioscoMostaza,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Últimos Movimientos:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    movimientosSimulados.forEach { mov ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(mov.detalle, color = Color.White, fontSize = 12.sp)
                                Text(mov.fecha, color = Color.Gray, fontSize = 11.sp)
                            }
                            Text(
                                text = mov.monto,
                                color = if (mov.monto.startsWith("+")) Color(0xFF4CAF50) else Color(0xFFFF5252),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.5f), thickness = 0.5.dp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoDetalle = false
                        montoACargar = ""
                        mostrarDialogoCarga = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KioscoMostaza, contentColor = KioscoNegro)
                ) {
                    Text("Gestionar Saldo")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoDetalle = false }) {
                    Text("Cerrar", color = KioscoDorado)
                }
            }
        )
    }

    // Diálogo Cargar / Modificar Saldo con Selección de Modo
    if (mostrarDialogoCarga && alumnoSeleccionado != null) {
        var esModoFijo by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { mostrarDialogoCarga = false },
            title = { Text("Gestionar Saldo", color = KioscoMostaza, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "Alumno: ${alumnoSeleccionado!!.nombreCompleto}",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "Saldo actual: ${formatearPrecio(alumnoSeleccionado!!.saldo)}",
                        color = KioscoDorado,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        RadioButton(
                            selected = !esModoFijo,
                            onClick = { esModoFijo = false },
                            colors = RadioButtonDefaults.colors(selectedColor = KioscoMostaza)
                        )
                        Text("Sumar al saldo", color = Color.White, fontSize = 13.sp)

                        Spacer(modifier = Modifier.width(12.dp))

                        RadioButton(
                            selected = esModoFijo,
                            onClick = { esModoFijo = true },
                            colors = RadioButtonDefaults.colors(selectedColor = KioscoMostaza)
                        )
                        Text("Modificar total", color = Color.White, fontSize = 13.sp)
                    }

                    OutlinedTextField(
                        value = montoACargar,
                        onValueChange = { montoACargar = it },
                        label = { Text(if (esModoFijo) "Nuevo Saldo Total ($)" else "Monto a Sumar ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedTextFieldColors()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val valorIngresado = montoACargar.trim().replace(',', '.').toDoubleOrNull() ?: 0.0
                        val alumno = alumnoSeleccionado
                        if (alumno != null) {
                            val saldoFinal = if (esModoFijo) valorIngresado else (alumno.saldo + valorIngresado)

                            db.collection("CLIENTES").document(alumno.dni)
                                .update("saldo", saldoFinal)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Saldo actualizado a: $saldoFinal", Toast.LENGTH_SHORT).show()
                                    mostrarDialogoCarga = false
                                    montoACargar = ""
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Error al actualizar saldo", Toast.LENGTH_SHORT).show()
                                }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KioscoMostaza, contentColor = KioscoNegro)
                ) { Text("Guardar Saldo") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoCarga = false }) { Text("Cancelar", color = KioscoDorado) }
            },
            containerColor = KioscoSuperficie
        )
    }

    // Diálogo Confirmar Eliminación
    if (mostrarDialogoEliminar && alumnoSeleccionado != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text("Eliminar Alumno", color = Color(0xFFFF5252)) },
            text = {
                Text(
                    "¿Seguro que querés eliminar a ${alumnoSeleccionado!!.nombreCompleto} " +
                            "(DNI ${alumnoSeleccionado!!.dni})? Esta acción no se puede deshacer.",
                    color = Color.White
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val alumno = alumnoSeleccionado
                        if (alumno != null) {
                            db.collection("CLIENTES").document(alumno.dni)
                                .delete()
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Alumno eliminado", Toast.LENGTH_SHORT).show()
                                    mostrarDialogoEliminar = false
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Error al eliminar el alumno", Toast.LENGTH_SHORT).show()
                                }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) { Text("Cancelar", color = KioscoDorado) }
            },
            containerColor = KioscoSuperficie
        )
    }

    // Diálogo Nuevo Alumno
    if (mostrarDialogoNuevoAlumno) {
        var nuevoNombre by remember { mutableStateOf("") }
        var nuevoApellido by remember { mutableStateOf("") }
        var nuevoDni by remember { mutableStateOf("") }
        var saldoInicial by remember { mutableStateOf("0") }

        AlertDialog(
            onDismissRequest = { mostrarDialogoNuevoAlumno = false },
            title = { Text("Nuevo Alumno", color = KioscoMostaza) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nuevoNombre,
                        onValueChange = { nuevoNombre = it },
                        label = { Text("Nombre") },
                        colors = outlinedTextFieldColors()
                    )
                    OutlinedTextField(
                        value = nuevoApellido,
                        onValueChange = { nuevoApellido = it },
                        label = { Text("Apellido") },
                        colors = outlinedTextFieldColors()
                    )
                    OutlinedTextField(
                        value = nuevoDni,
                        onValueChange = { nuevoDni = it.filter { c -> c.isDigit() } },
                        label = { Text("DNI") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = outlinedTextFieldColors()
                    )
                    OutlinedTextField(
                        value = saldoInicial,
                        onValueChange = { saldoInicial = it },
                        label = { Text("Saldo Inicial") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = outlinedTextFieldColors()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nuevoNombre.isNotBlank() && nuevoDni.isNotBlank()) {
                            val data = mapOf(
                                "nombre" to nuevoNombre.trim(),
                                "apellido" to nuevoApellido.trim(),
                                "saldo" to (saldoInicial.toDoubleOrNull() ?: 0.0)
                            )
                            db.collection("CLIENTES").document(nuevoDni.trim()).set(data)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Alumno agregado", Toast.LENGTH_SHORT).show()
                                    mostrarDialogoNuevoAlumno = false
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Error al agregar el alumno", Toast.LENGTH_SHORT).show()
                                }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KioscoMostaza)
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoNuevoAlumno = false }) { Text("Cancelar", color = KioscoDorado) }
            },
            containerColor = KioscoSuperficie
        )
    }
}

@Composable
fun TarjetaGestionAlumno(
    alumno: Alumno,
    onClickTarjeta: () -> Unit,
    onCargarSaldo: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickTarjeta() },
        colors = CardDefaults.cardColors(containerColor = KioscoSuperficie),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, KioscoDorado.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = alumno.nombreCompleto, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = "DNI: ${alumno.dni}", color = KioscoTextoSecundario, fontSize = 14.sp)
                }
                Text(
                    text = formatearPrecio(alumno.saldo),
                    color = KioscoMostaza,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onCargarSaldo,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = KioscoMostaza, contentColor = KioscoNegro),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.AddCard, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cargar Saldo", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onEliminar,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                    border = BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Eliminar", fontSize = 12.sp)
                }
            }
        }
    }
}

private fun formatearPrecio(precio: Double): String {
    val formato = NumberFormat.getCurrencyInstance(Locale("es", "AR"))
    return formato.format(precio)
}

@Composable
private fun outlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = KioscoMostaza,
    unfocusedBorderColor = KioscoDorado.copy(alpha = 0.5f),
    focusedLabelColor = KioscoMostaza,
    unfocusedLabelColor = KioscoDorado,
    cursorColor = KioscoMostaza,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)