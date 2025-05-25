package com.example.whatsapp_gps

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.example.sendmessagewhatsapp.Ubicacion
import androidx.core.net.toUri
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whatsapp_gps.ui.theme.Whatsapp_GPSTheme

class MainActivity : ComponentActivity() {

    private lateinit var ubicacion: Ubicacion

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permiso concedido, se puede obtener ubicación
                obtenerYEnviarUbicacion()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ubicacion = Ubicacion(this)

        setContent {
            Whatsapp_GPSTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    SendWhatsAppMessageWithLocation(
                        modifier = Modifier.padding(paddingValues),
                        onRequestLocationPermission = {
                            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        },
                        onSendMessageWithLocation = { phoneNumber, message ->
                            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                obtenerYEnviarUbicacion(phoneNumber, message)
                            } else {
                                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        }
                    )
                }
            }
        }
    }

    private fun obtenerYEnviarUbicacion(phoneNumber: String = "", message: String = "") {
        ubicacion.getCurrentLocation { location: Location? ->
            val context = this
            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude
                val fullMessage = if (message.isNotBlank()) {
                    "$message\nMi ubicación actual: https://maps.google.com/?q=$lat,$lon"
                } else {
                    "Mi ubicación actual: https://maps.google.com/?q=$lat,$lon"
                }
                val uri = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${fullMessage}".toUri()
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = uri
                    setPackage("com.whatsapp")
                }

                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(context, "WhatsApp no está instalado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun SendWhatsAppMessageWithLocation(
    modifier: Modifier = Modifier,
    onRequestLocationPermission: () -> Unit,
    onSendMessageWithLocation: (phoneNumber: String, message: String) -> Unit
) {
    val context = LocalContext.current

    var phoneNumber by remember { mutableStateOf("+507") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Enviar Mensaje y Ubicación por WhatsApp",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "José Hernández")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Número de telefono") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Mensaje") },
            singleLine = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                onSendMessageWithLocation(phoneNumber, message)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Whatsapp_GPSTheme {
        SendWhatsAppMessageWithLocation(
            onRequestLocationPermission = {},
            onSendMessageWithLocation = { _, _ -> }
        )
    }
}
