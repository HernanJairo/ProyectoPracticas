package com.ejemplo.kioscoapp.pantallas

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.ejemplo.kioscoapp.ui.theme.KioscoSuperficieVariante
import com.ejemplo.kioscoapp.ui.theme.KioscoTextoSecundario
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PantallaGestionUsuarios(
    navController: NavController, 
    rol: String, 
    nombreUsuario: String, 
    idAdminActual: String
) {
    val context = LocalContext.current
    val db = remember { FirebaseFirestore.getInstance() }
    
    val usuariosList = remember { mutableStateListOf<Map<String, Any>>() }
    var mostrarDialogoNuevo by remember { mutableStateOf(false) }

    fun cargarUsuarios() {
        db.collection("USUARIOS").get()
            .addOnSuccessListener { snapshot ->
                usuariosList.clear()
                snapshot.documents.forEach { doc ->
                    // Requisito: El admin actual NO puede eliminarse a sí mismo (oculto en la lista)
                    if (doc.id != idAdminActual) {
                        val data = doc.data?.toMutableMap() ?: mutableMapOf()
                        data["id"] = doc.id
                        usuariosList.add(data)
                    }
                }
            }
    }

    LaunchedEffect(Unit) {
        cargarUsuarios()
    }

    Scaffold(
        topBar = { 
            BarraSuperior(
                titulo = "Gestión de Personal",
                nombreUsuario = nombreUsuario,
                rolUsuario = rol,
                navController = navController,
                mostrarBotonAtras = true
            ) 
        },
        bottomBar = { BarraInferior(navController = navController, rol = rol) },
        containerColor = KioscoNegro
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Usuarios Registrados", color = KioscoMostaza, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = { mostrarDialogoNuevo = true }) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = KioscoMostaza)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (usuariosList.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("No hay otros usuarios registrados", color = KioscoTextoSecundario)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(usuariosList) { usuario ->
                        ItemUsuario(
                            usuario = usuario,
                            onEliminar = {
                                val id = usuario["id"].toString()
                                db.collection("USUARIOS").document(id).delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                                        cargarUsuarios()
                                    }
                            }
                        )
                    }
                }
            }
        }
    }

    if (mostrarDialogoNuevo) {
        var user by remember { mutableStateOf("") }
        var pass by remember { mutableStateOf("") }
        var selectedRol by remember { mutableStateOf("vendedor") }

        AlertDialog(
            onDismissRequest = { mostrarDialogoNuevo = false },
            title = { Text("Nuevo Usuario", color = KioscoMostaza) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = user, onValueChange = { user = it }, label = { Text("Usuario") }, colors = outlinedTextFieldColors())
                    OutlinedTextField(value = pass, onValueChange = { pass = it }, label = { Text("Contraseña") }, colors = outlinedTextFieldColors())
                    
                    Text("Rol:", color = Color.White, modifier = Modifier.padding(top = 8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selectedRol == "vendedor", onClick = { selectedRol = "vendedor" })
                        Text("Vendedor", color = Color.White)
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(selected = selectedRol == "admin", onClick = { selectedRol = "admin" })
                        Text("Admin", color = Color.White)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (user.isNotBlank() && pass.isNotBlank()) {
                            val data = mapOf(
                                "usuario" to user,
                                "contrasena" to pass,
                                "rol" to selectedRol
                            )
                            db.collection("USUARIOS").add(data)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Usuario creado", Toast.LENGTH_SHORT).show()
                                    cargarUsuarios()
                                    mostrarDialogoNuevo = false
                                }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KioscoMostaza)
                ) { Text("Crear") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoNuevo = false }) { Text("Cancelar", color = KioscoDorado) }
            },
            containerColor = KioscoSuperficie
        )
    }
}

@Composable
fun ItemUsuario(usuario: Map<String, Any>, onEliminar: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = KioscoSuperficieVariante),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = usuario["usuario"].toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "Rol: ${usuario["rol"]}", color = KioscoDorado, fontSize = 14.sp)
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFFF5252))
            }
        }
    }
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
