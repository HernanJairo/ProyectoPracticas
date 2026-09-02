package com.ejemplo.kioscoapp.navegacion

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ejemplo.kioscoapp.pantallas.*
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun NavegacionApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val db = remember { FirebaseFirestore.getInstance() }
    
    // Estado global del usuario logueado
    var rolUsuario by remember { mutableStateOf("") }
    var nombreUsuario by remember { mutableStateOf("") }
    var idUsuarioActual by remember { mutableStateOf("") } // Guardamos ID para evitar auto-eliminación

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            PantallaLogin(
                onIrACatalogo = { 
                    rolUsuario = "invitado"
                    nombreUsuario = "Invitado"
                    navController.navigate("catalogo_invitado") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onConsultarDniAlumno = { dniIngresado: String, onResultado: (String, Double) -> Unit, onError: () -> Unit ->
                    db.collection("CLIENTES").document(dniIngresado).get()
                        .addOnSuccessListener { doc ->
                            if (doc.exists()) {
                                val nombre = doc.getString("nombre") ?: "Alumno"
                                // LEER SALDO (soporta Int, Long o Double en Firestore)
                                val saldo = (doc.get("saldo") as? Number)?.toDouble() ?: 0.0
                                onResultado(nombre, saldo)
                            } else {
                                onError()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error al conectar con la base de datos", Toast.LENGTH_SHORT).show()
                            onError()
                        }
                },
                onIniciarSesionPersonal = { usuario: String, contrasena: String ->
                    db.collection("USUARIOS")
                        .whereEqualTo("usuario", usuario)
                        .whereEqualTo("contrasena", contrasena)
                        .get()
                        .addOnSuccessListener { snapshot ->
                            if (!snapshot.isEmpty) {
                                val doc = snapshot.documents[0]
                                val rol = doc.getString("rol")?.lowercase() ?: ""
                                val nombre = doc.getString("usuario") ?: "Usuario"
                                
                                rolUsuario = rol
                                nombreUsuario = nombre
                                idUsuarioActual = doc.id
                                
                                Toast.makeText(context, "Bienvenido $nombre ($rol)", Toast.LENGTH_SHORT).show()
                                
                                if (rol == "admin" || rol == "administrador") {
                                    navController.navigate("reportes") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    navController.navigate("ventas") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
                        }
                }
            )
        }

        composable("catalogo_invitado") {
            PantallaCatalogo(navController = navController, esInvitado = true, rol = "invitado", nombreUsuario = nombreUsuario)
        }

        composable("catalogo") {
            PantallaCatalogo(navController = navController, esInvitado = false, rol = rolUsuario, nombreUsuario = nombreUsuario)
        }

        composable("ventas") { 
            PantallaVentas(navController = navController, rol = rolUsuario, nombreUsuario = nombreUsuario) 
        }
        composable("reportes") { 
            PantallaReportes(navController = navController, rol = rolUsuario, nombreUsuario = nombreUsuario) 
        }
        composable("vencimientos") { 
            PantallaVencimientos(navController = navController, rol = rolUsuario, nombreUsuario = nombreUsuario) 
        }
        composable("productos") { 
            PantallaProductos(navController = navController, rol = rolUsuario, nombreUsuario = nombreUsuario) 
        }
        
        composable("gestion_alumnos") { 
            PantallaGestionAlumnos(navController = navController, rol = rolUsuario, nombreUsuario = nombreUsuario) 
        }
        
        composable("gestion_usuarios") { 
            PantallaGestionUsuarios(
                navController = navController, 
                rol = rolUsuario, 
                nombreUsuario = nombreUsuario,
                idAdminActual = idUsuarioActual
            ) 
        }
    }
}
