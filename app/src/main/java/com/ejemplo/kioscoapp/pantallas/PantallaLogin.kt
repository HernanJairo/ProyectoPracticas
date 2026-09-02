package com.ejemplo.kioscoapp.pantallas

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ejemplo.kioscoapp.ui.theme.KioscoDorado
import com.ejemplo.kioscoapp.ui.theme.KioscoMostaza
import com.ejemplo.kioscoapp.ui.theme.KioscoNegro
import com.ejemplo.kioscoapp.ui.theme.KioscoSuperficie
import com.ejemplo.kioscoapp.ui.theme.KioscoSuperficieVariante
import com.ejemplo.kioscoapp.ui.theme.KioscoTextoSecundario
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

private const val WHATSAPP_NUMERO = "5493884344567"
private const val WHATSAPP_TEXTO_DEFECTO = "Hola, quería consultar sobre el kiosco del instituto."
private const val INSTAGRAM_USUARIO = "kiosco.instituto"

data class CompraSimulada(
    val detalle: String,
    val fecha: String,
    val monto: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLogin(
    onIrACatalogo: () -> Unit,
    onConsultarDniAlumno: (String, (String, Double) -> Unit, () -> Unit) -> Unit,
    onIniciarSesionPersonal: (String, String) -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    var dniAlumno by remember { mutableStateOf("") }
    var usuarioPersonal by remember { mutableStateOf("") }
    var contrasenaPersonal by remember { mutableStateOf("") }
    var verContrasena by remember { mutableStateOf(false) }

    var cargandoAlumno by remember { mutableStateOf(false) }
    var mostrarErrorAlumno by remember { mutableStateOf(false) }
    var mostrarDialogoContacto by remember { mutableStateOf(false) }

    // Datos del alumno para el modal de resultado
    var nombreAlumnoModal by remember { mutableStateOf<String?>(null) }
    var saldoAlumnoModal by remember { mutableStateOf<Double?>(null) }
    var historialAlumnoModal by remember { mutableStateOf<List<CompraSimulada>>(emptyList()) }
    var mostrarDialogoAlumno by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            // Barra superior de accesos directos: Catálogo (izq) y Contacto (der)
            TopAppBar(
                title = {},
                navigationIcon = {
                    TextButton(onClick = onIrACatalogo) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = "Catálogo", tint = KioscoMostaza)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Catálogo", color = KioscoMostaza)
                    }
                },
                actions = {
                    TextButton(onClick = { mostrarDialogoContacto = true }) {
                        Text("Contacto", color = KioscoMostaza)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ContactPhone, contentDescription = "Contacto", tint = KioscoMostaza)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KioscoNegro)
            )
        },
        containerColor = KioscoNegro
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScrollFallback()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Storefront,
                contentDescription = null,
                tint = KioscoMostaza,
                modifier = Modifier.size(80.dp)
            )

            Text(
                text = "Kiosco App",
                color = KioscoMostaza,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Sistema de Gestión y Ventas",
                color = KioscoDorado,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // SECCIÓN ALUMNOS (Consulta de Saldo) - solo DNI
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = KioscoSuperficie),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, KioscoDorado.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Consulta de Alumnos",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Ingresá tu DNI para ver tu saldo disponible",
                        color = KioscoTextoSecundario,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = dniAlumno,
                        onValueChange = {
                            dniAlumno = it
                            mostrarErrorAlumno = false
                        },
                        label = { Text("DNI del Alumno") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = outlinedTextFieldColors()
                    )

                    if (mostrarErrorAlumno) {
                        Text(
                            text = "DNI no encontrado. Verificá los datos.",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Button(
                        onClick = {
                            if (dniAlumno.isNotBlank()) {
                                cargandoAlumno = true
                                onConsultarDniAlumno(dniAlumno, { nombre, saldo ->
                                    nombreAlumnoModal = nombre
                                    saldoAlumnoModal = saldo
                                    historialAlumnoModal = generarHistorialSimulado(nombre)
                                    cargandoAlumno = false
                                    mostrarDialogoAlumno = true
                                }, {
                                    mostrarErrorAlumno = true
                                    cargandoAlumno = false
                                })
                            }
                        },
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = KioscoMostaza, contentColor = KioscoNegro),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !cargandoAlumno && dniAlumno.isNotBlank()
                    ) {
                        if (cargandoAlumno) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = KioscoNegro, strokeWidth = 2.dp)
                        } else {
                            Text("Consultar Saldo")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SECCIÓN PERSONAL (Ingreso con Usuario/Contraseña)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = KioscoSuperficieVariante),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ingreso de Personal",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = usuarioPersonal,
                        onValueChange = { usuarioPersonal = it },
                        label = { Text("Usuario") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = KioscoDorado) },
                        shape = RoundedCornerShape(12.dp),
                        colors = outlinedTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = contrasenaPersonal,
                        onValueChange = { contrasenaPersonal = it },
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (verContrasena) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = KioscoDorado) },
                        trailingIcon = {
                            IconButton(onClick = { verContrasena = !verContrasena }) {
                                Icon(
                                    imageVector = if (verContrasena) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = KioscoDorado
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = outlinedTextFieldColors()
                    )

                    Button(
                        onClick = { onIniciarSesionPersonal(usuarioPersonal, contrasenaPersonal) },
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = KioscoDorado, contentColor = KioscoNegro),
                        shape = RoundedCornerShape(12.dp),
                        enabled = usuarioPersonal.isNotBlank() && contrasenaPersonal.isNotBlank()
                    ) {
                        Text("Iniciar Sesión")
                    }
                }
            }
        }
    }

    // Modal con el resultado de la consulta de alumno (nombre, saldo e historial)
    if (mostrarDialogoAlumno && nombreAlumnoModal != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoAlumno = false },
            title = { Text("Hola, $nombreAlumnoModal!", color = KioscoMostaza) },
            text = {
                Column {
                    Text(
                        text = "Saldo actual:",
                        color = KioscoDorado,
                        fontSize = 12.sp
                    )
                    Text(
                        text = formatearPrecio(saldoAlumnoModal ?: 0.0),
                        color = KioscoMostaza,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Últimos movimientos",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    historialAlumnoModal.forEach { compra ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = compra.detalle, color = Color.White, fontSize = 13.sp)
                                Text(text = compra.fecha, color = KioscoTextoSecundario, fontSize = 11.sp)
                            }
                            Text(
                                text = "-${formatearPrecio(compra.monto)}",
                                color = Color(0xFFFF8A65),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoAlumno = false }) {
                    Text("Cerrar", color = KioscoMostaza)
                }
            },
            containerColor = KioscoSuperficie
        )
    }

    // Diálogo de Contacto (WhatsApp e Instagram)
    if (mostrarDialogoContacto) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoContacto = false },
            title = { Text("Contacto", color = KioscoMostaza) },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = Color(0xFF25D366))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("WhatsApp", color = Color.White, fontWeight = FontWeight.Medium)
                            Text("+54 388 434-4567", color = KioscoTextoSecundario, fontSize = 12.sp)
                        }
                    }
                    TextButton(
                        onClick = {
                            try {
                                val texto = java.net.URLEncoder.encode(WHATSAPP_TEXTO_DEFECTO, "UTF-8")
                                uriHandler.openUri("https://wa.me/$WHATSAPP_NUMERO?text=$texto")
                            } catch (_: Exception) {
                                Toast.makeText(context, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Abrir WhatsApp", color = Color(0xFF25D366))
                    }

                    HorizontalDivider(color = KioscoDorado.copy(alpha = 0.2f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = KioscoMostaza)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Instagram", color = Color.White, fontWeight = FontWeight.Medium)
                            Text("@$INSTAGRAM_USUARIO", color = KioscoTextoSecundario, fontSize = 12.sp)
                        }
                    }
                    TextButton(
                        onClick = {
                            try {
                                uriHandler.openUri("https://instagram.com/$INSTAGRAM_USUARIO")
                            } catch (_: Exception) {
                                Toast.makeText(context, "No se pudo abrir el navegador", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Abrir Instagram", color = KioscoMostaza)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoContacto = false }) {
                    Text("Cerrar", color = KioscoDorado)
                }
            },
            containerColor = KioscoSuperficie
        )
    }
}

/**
 * Genera una simulación estable (por nombre) de las últimas 3 compras/cargas de un alumno.
 * En un futuro esto podría reemplazarse por una consulta real a una subcolección "COMPRAS".
 */
private fun generarHistorialSimulado(nombre: String): List<CompraSimulada> {
    val detalles = listOf(
        "Golosinas surtidas",
        "Snack + Bebida",
        "Combo Merienda",
        "Galletitas y jugo",
        "Recarga de saldo"
    )
    val random = Random(nombre.hashCode())
    val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale("es", "AR"))
    val calendario = Calendar.getInstance()

    return (0 until 3).map {
        calendario.add(Calendar.DAY_OF_YEAR, -random.nextInt(1, 5))
        val monto = 200.0 + random.nextInt(1, 20) * 50.0
        CompraSimulada(
            detalle = detalles[random.nextInt(detalles.size)],
            fecha = formatoFecha.format(calendario.time),
            monto = monto
        )
    }
}

private fun formatearPrecio(precio: Double): String {
    val formato = NumberFormat.getCurrencyInstance(Locale("es", "AR"))
    return formato.format(precio)
}

@Composable
private fun Modifier.verticalScrollFallback(): Modifier {
    // Envuelve el contenido con scroll vertical para que no se corte en pantallas chicas
    val scrollState = rememberScrollState()
    return this.then(Modifier.verticalScroll(scrollState))
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