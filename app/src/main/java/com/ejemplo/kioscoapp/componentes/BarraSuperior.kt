package com.ejemplo.kioscoapp.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ejemplo.kioscoapp.ui.theme.KioscoDorado
import com.ejemplo.kioscoapp.ui.theme.KioscoMostaza
import com.ejemplo.kioscoapp.ui.theme.KioscoNegro
import com.ejemplo.kioscoapp.ui.theme.KioscoSuperficie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(
    titulo: String,
    mostrarBotonAtras: Boolean = false,
    navController: NavController? = null,
    nombreUsuario: String = "",
    rolUsuario: String = "",
    esInvitado: Boolean = false
) {
    var menuUsuarioExpandido by remember { mutableStateOf(false) }
    var mostrarDialogoSoporte by remember { mutableStateOf(false) }

    val esAdmin = rolUsuario == "admin" || rolUsuario == "administrador"
    val esVendedor = rolUsuario == "vendedor"

    TopAppBar(
        title = {
            Text(
                text = titulo,
                color = KioscoMostaza,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            if (mostrarBotonAtras && navController != null) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = KioscoMostaza
                    )
                }
            }
        },
        actions = {
            // Si es invitado, se oculta por completo el avatar de perfil
            if (!esInvitado) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    if (nombreUsuario.isNotBlank()) {
                        Text(
                            text = nombreUsuario,
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }

                    Box {
                        IconButton(onClick = { menuUsuarioExpandido = true }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Perfil de usuario",
                                tint = KioscoMostaza,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = menuUsuarioExpandido,
                            onDismissRequest = { menuUsuarioExpandido = false },
                            modifier = Modifier.background(KioscoSuperficie)
                        ) {
                            // Menú contextual: Admin
                            if (esAdmin) {
                                DropdownMenuItem(
                                    text = { Text("Gestión de Personal/Usuarios", color = Color.White) },
                                    leadingIcon = {
                                        Icon(Icons.Default.ManageAccounts, contentDescription = null, tint = KioscoDorado)
                                    },
                                    onClick = {
                                        menuUsuarioExpandido = false
                                        navController?.navigate("gestion_usuarios")
                                    }
                                )
                            }

                            // Menú contextual: Vendedor
                            if (esVendedor) {
                                DropdownMenuItem(
                                    text = { Text("Notificar Error / Soporte", color = Color.White) },
                                    leadingIcon = {
                                        Icon(Icons.Default.SupportAgent, contentDescription = null, tint = KioscoDorado)
                                    },
                                    onClick = {
                                        menuUsuarioExpandido = false
                                        mostrarDialogoSoporte = true
                                    }
                                )
                            }

                            DropdownMenuItem(
                                text = { Text("Cerrar Sesión", color = Color(0xFFFF5252)) },
                                leadingIcon = {
                                    Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFFFF5252))
                                },
                                onClick = {
                                    menuUsuarioExpandido = false
                                    navController?.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = KioscoNegro)
    )

    // Diálogo informativo de Soporte (solo Vendedor)
    if (mostrarDialogoSoporte) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoSoporte = false },
            title = { Text("Notificar Error / Soporte", color = KioscoMostaza) },
            text = {
                Text(
                    "Si encontraste un error o necesitás ayuda, comunicate con el " +
                            "administrador del sistema por WhatsApp o Instagram desde la " +
                            "sección de Contacto en la pantalla de inicio de sesión.",
                    color = Color.White
                )
            },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoSoporte = false }) {
                    Text("Entendido", color = KioscoMostaza)
                }
            },
            containerColor = KioscoSuperficie
        )
    }
}