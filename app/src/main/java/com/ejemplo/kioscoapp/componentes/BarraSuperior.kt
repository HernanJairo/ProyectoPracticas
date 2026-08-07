package com.ejemplo.kioscoapp.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
    navController: NavController? = null
) {
    var menuUsuarioExpandido by remember { mutableStateOf(false) }
    var mostrarDialogoProblema by remember { mutableStateOf(false) }

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
                    DropdownMenuItem(
                        text = { Text("Notificar un problema", color = Color.White) },
                        leadingIcon = { Icon(Icons.Default.BugReport, contentDescription = null, tint = KioscoDorado) },
                        onClick = {
                            menuUsuarioExpandido = false
                            mostrarDialogoProblema = true
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Cerrar Sesión", color = Color(0xFFFF5252)) },
                        leadingIcon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFFFF5252)) },
                        onClick = {
                            menuUsuarioExpandido = false
                            navController?.navigate("login") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = KioscoNegro)
    )

    if (mostrarDialogoProblema) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoProblema = false },
            containerColor = KioscoSuperficie,
            title = { Text("Notificar Problema", color = KioscoMostaza) },
            text = { Text("Escribí el inconveniente técnico para enviarlo al administrador del sistema.", color = Color.White) },
            confirmButton = {
                Button(
                    onClick = { mostrarDialogoProblema = false },
                    colors = ButtonDefaults.buttonColors(containerColor = KioscoMostaza, contentColor = KioscoNegro)
                ) { Text("Enviar Reporte") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoProblema = false }) {
                    Text("Cancelar", color = KioscoDorado)
                }
            }
        )
    }
}