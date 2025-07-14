package com.example.calc

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.calc.ui.theme.CalcTheme

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val nombre = intent.getStringExtra("username") ?: "Invitado"
        setContent {
            CalcTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Calculadora(nombre)
                }
            }
        }
    }
}

@Composable

fun Calculadora(nombre: String) {
    val context = LocalContext.current
    var num1 by remember { mutableStateOf("") }
    var num2 by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1C1C)) // Fondo oscuro
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Calculadora",
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFFF5F5F5) // Blanco humo
        )

        Text(
            text = "Bienvenido: $nombre",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFAAAAAA)
        )

        val textFieldColors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFA9A9A9), // Gris acero claro
            unfocusedContainerColor = Color(0xFFA9A9A9),
            disabledContainerColor = Color(0xFFA9A9A9),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )

        TextField(
            value = num1,
            onValueChange = { num1 = it },
            label = { Text("Primer número") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        TextField(
            value = num2,
            onValueChange = { num2 = it },
            label = { Text("Segundo número") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        val buttonColor = ButtonDefaults.buttonColors(containerColor = Color(0xFF444444))

        // Fila 1
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    val n1 = num1.toFloatOrNull()
                    val n2 = num2.toFloatOrNull()
                    if (n1 != null && n2 != null) resultado = (n1 + n2).toString()
                },
                colors = buttonColor,
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text("+", color = Color.White)
            }

            Button(
                onClick = {
                    val n1 = num1.toFloatOrNull()
                    val n2 = num2.toFloatOrNull()
                    if (n1 != null && n2 != null) resultado = (n1 - n2).toString()
                },
                colors = buttonColor,
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text("-", color = Color.White)
            }
        }

        // Fila 2
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    val n1 = num1.toFloatOrNull()
                    val n2 = num2.toFloatOrNull()
                    if (n1 != null && n2 != null) resultado = (n1 * n2).toString()
                },
                colors = buttonColor,
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text("*", color = Color.White)
            }

            Button(
                onClick = {
                    val n1 = num1.toFloatOrNull()
                    val n2 = num2.toFloatOrNull()
                    resultado = if (n1 != null && n2 != null && n2 != 0f)
                        (n1 / n2).toString()
                    else
                        "División inválida"
                },
                colors = buttonColor,
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text("/", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("Resultado", color = Color(0xFFF5F5F5))

        TextField(
            value = resultado,
            onValueChange = {},
            readOnly = true,
            label = { Text("Resultado") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        Row(horizontalArrangement = Arrangement.Center) {
            Button(
                onClick = { (context as? ComponentActivity)?.finish() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000)) // rojo industrial
            ) {
                Text("Salir", color = Color.White)
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun CalculadoraPreview() {
    CalcTheme {
        Calculadora("Usuario")
    }
}
