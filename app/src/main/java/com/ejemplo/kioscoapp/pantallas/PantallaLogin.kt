package com.ejemplo.kioscoapp.pantallas

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ejemplo.kioscoapp.ui.theme.KioscoDorado
import com.ejemplo.kioscoapp.ui.theme.KioscoMostaza
import com.ejemplo.kioscoapp.ui.theme.KioscoNegro

private enum class RolLogin(val etiqueta: String) {
    ALUMNO("Alumno"),
    VENDEDORA("Vendedora"),
    ADMINISTRADOR("Administrador")
}

private const val WHATSAPP_NUMERO = "+54 388 123-4567"
private const val WHATSAPP_NUMERO_LIMPIO = "543881234567"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLogin(
    modifier: Modifier = Modifier,
    onIrACatalogo: () -> Unit = {},
    onIngresoAlumno: (dni: String) -> Unit = {},
    onIniciarSesion: (usuario: String, contrasena: String, rol: String) -> Unit = { _, _, _ -> },
    onGoogleSignIn: () -> Unit = {},
    onFacebookSignIn: () -> Unit = {}
) {
    var rolSeleccionado by remember { mutableStateOf(RolLogin.ALUMNO) }
    var menuRolExpandido by remember { mutableStateOf(false) }

    var dni by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var contrasenaVisible by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var mostrarDialogoContacto by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val coloresCampo = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = KioscoMostaza,
        unfocusedBorderColor = KioscoDorado.copy(alpha = 0.5f),
        focusedLabelColor = KioscoMostaza,
        unfocusedLabelColor = KioscoDorado,
        cursorColor = KioscoMostaza,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
    )

    fun limpiarCamposAlCambiarRol() {
        dni = ""
        usuario = ""
        contrasena = ""
        mensajeError = null
        contrasenaVisible = false
    }

    fun intentarAccionPrincipal() {
        focusManager.clearFocus()
        when (rolSeleccionado) {
            RolLogin.ALUMNO -> {
                if (dni.isBlank()) {
                    mensajeError = "Ingresá tu número de documento (DNI)"
                } else {
                    mensajeError = null
                    onIngresoAlumno(dni.trim())
                }
            }
            RolLogin.VENDEDORA, RolLogin.ADMINISTRADOR -> {
                when {
                    usuario.isBlank() || contrasena.isBlank() -> {
                        mensajeError = "Completa usuario/correo y contraseña"
                    }
                    else -> {
                        mensajeError = null
                        onIniciarSesion(usuario.trim(), contrasena, rolSeleccionado.etiqueta)
                    }
                }
            }
        }
    }

    fun abrirWhatsApp() {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://wa.me/$WHATSAPP_NUMERO_LIMPIO")
        )
        context.startActivity(intent)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(KioscoNegro)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onIrACatalogo) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Ir al catálogo",
                        tint = KioscoMostaza,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = " Catálogo",
                        color = KioscoMostaza,
                        fontWeight = FontWeight.Medium
                    )
                }

                TextButton(onClick = { mostrarDialogoContacto = true }) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "Contacto WhatsApp",
                        tint = KioscoDorado,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = " Contacto",
                        color = KioscoDorado,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Kiosco",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = KioscoMostaza,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Bienvenido — elegí tu rol para continuar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            ExposedDropdownMenuBox(
                expanded = menuRolExpandido,
                onExpandedChange = { menuRolExpandido = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = rolSeleccionado.etiqueta,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Rol") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Desplegar roles",
                            tint = KioscoMostaza
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = coloresCampo,
                    shape = RoundedCornerShape(12.dp)
                )

                ExposedDropdownMenu(
                    expanded = menuRolExpandido,
                    onDismissRequest = { menuRolExpandido = false },
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    RolLogin.entries.forEach { rol ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = rol.etiqueta,
                                    color = if (rol == rolSeleccionado) {
                                        KioscoMostaza
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    },
                                    fontWeight = if (rol == rolSeleccionado) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.Normal
                                    }
                                )
                            },
                            onClick = {
                                rolSeleccionado = rol
                                menuRolExpandido = false
                                limpiarCamposAlCambiarRol()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (rolSeleccionado) {
                RolLogin.ALUMNO -> {
                    OutlinedTextField(
                        value = dni,
                        onValueChange = {
                            dni = it.filter { c -> c.isDigit() }
                            mensajeError = null
                        },
                        label = { Text("Número de Documento (DNI)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = mensajeError != null,
                        colors = coloresCampo,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { intentarAccionPrincipal() }
                        )
                    )
                }

                RolLogin.VENDEDORA, RolLogin.ADMINISTRADOR -> {
                    OutlinedTextField(
                        value = usuario,
                        onValueChange = {
                            usuario = it
                            mensajeError = null
                        },
                        label = { Text("Usuario / Correo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = mensajeError != null && usuario.isBlank(),
                        colors = coloresCampo,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = {
                            contrasena = it
                            mensajeError = null
                        },
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = mensajeError != null && contrasena.isBlank(),
                        visualTransformation = if (contrasenaVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            IconButton(onClick = { contrasenaVisible = !contrasenaVisible }) {
                                Icon(
                                    imageVector = if (contrasenaVisible) {
                                        Icons.Default.VisibilityOff
                                    } else {
                                        Icons.Default.Visibility
                                    },
                                    contentDescription = if (contrasenaVisible) {
                                        "Ocultar contraseña"
                                    } else {
                                        "Mostrar contraseña"
                                    },
                                    tint = KioscoDorado
                                )
                            }
                        },
                        colors = coloresCampo,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { intentarAccionPrincipal() }
                        )
                    )
                }
            }

            mensajeError?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = { intentarAccionPrincipal() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = KioscoMostaza,
                    contentColor = KioscoNegro
                )
            ) {
                Text(
                    text = if (rolSeleccionado == RolLogin.ALUMNO) "Ingresar" else "Iniciar Sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (rolSeleccionado == RolLogin.VENDEDORA || rolSeleccionado == RolLogin.ADMINISTRADOR) {
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = KioscoDorado.copy(alpha = 0.3f)
                    )
                    Text(
                        text = "  o  ",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = KioscoDorado.copy(alpha = 0.3f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(
                    onClick = onGoogleSignIn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, KioscoDorado.copy(alpha = 0.6f))
                ) {
                    Text(text = "Continuar con Google", fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onFacebookSignIn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, KioscoDorado.copy(alpha = 0.6f))
                ) {
                    Text(text = "Continuar con Facebook", fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (mostrarDialogoContacto) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoContacto = false },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            title = {
                Text(
                    text = "Contacto del Kiosco",
                    color = KioscoMostaza,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "¿Tenés consultas? Escribinos por WhatsApp:",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = WHATSAPP_NUMERO,
                        color = KioscoDorado,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Horario: Lun a Vie, 7:30 – 17:00 hs",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoContacto = false
                        abrirWhatsApp()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KioscoMostaza,
                        contentColor = KioscoNegro
                    )
                ) {
                    Text("Abrir WhatsApp")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoContacto = false }) {
                    Text("Cerrar", color = KioscoDorado)
                }
            }
        )
    }
}