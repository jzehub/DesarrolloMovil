package com.example.mijuego

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mijuego.ui.theme.MiJuegoTheme
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiJuegoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Principal(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Principal(modifier: Modifier = Modifier) {
    var numero by remember { mutableStateOf("") }
    val numeroSecreto = remember { Random.nextInt(0, 101) }
    var intentos by remember { mutableStateOf(3) }
    var mensaje by remember { mutableStateOf("") }
    var tiempo by remember {mutableStateOf(30)}
    val focusManager = LocalFocusManager.current


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Adivina el número", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "José Hernández")
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Tienes $intentos intento(s) restante(s)")
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = numero,
            onValueChange = { numero = it },
            label = { Text("Tu adivinanza") },
            singleLine = true,
            enabled = intentos > 0 && mensaje != "¡Correcto!"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val adivinanza = numero.toIntOrNull()
                if (adivinanza == null) {
                    mensaje = "Por favor, ingresa un número válido"
                } else {
                    if (adivinanza == numeroSecreto) {
                        mensaje = "¡Correcto!"
                    } else {
                        intentos--
                        mensaje = if (intentos == 0) {
                            "¡Perdiste! El número era $numeroSecreto"
                        } else if (adivinanza < numeroSecreto) {
                            "Demasiado bajo"
                        } else {
                            "Demasiado alto"
                        }
                    }
                }
                focusManager.clearFocus()
                numero = ""
            },
            enabled = intentos > 0 && mensaje != "¡Correcto!" && tiempo >0
        ) {
            Text("Adivinar")
        }


        Spacer(modifier = Modifier.height(16.dp))
        if (mensaje.isNotEmpty()) {
            Text(text = mensaje)
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MiJuegoTheme {
        Principal()
    }
}
