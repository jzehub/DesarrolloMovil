package com.example.bingo_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BingoApp()
        }
    }
}

@Composable
fun BingoApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "inicio") {
        composable("inicio") {
            PantallaInicial(onStartGame = { matrixSize, uid ->
                navController.navigate("bingo/$matrixSize/$uid")
            })
        }
        composable(
            route = "bingo/{size}/{uid}",
            arguments = listOf(
                navArgument("size") { type = NavType.IntType },
                navArgument("uid") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val size = backStackEntry.arguments?.getInt("size") ?: 5
            val uid = backStackEntry.arguments?.getString("uid") ?: "UNKNOWN"
            Activity2(size, uid)
        }
    }
}

@Composable
fun PantallaInicial(onStartGame: (Int, String) -> Unit) {
    var matrixSizeText by remember { mutableStateOf("") }
    var uid by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B1B2F))
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "🎰 BINGO MANÍA 🎰",
            color = Color.Yellow,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = matrixSizeText,
            onValueChange = { matrixSizeText = it },
            label = { Text("Tamaño de la carta (ej: 5)", color = Color.White) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Cyan,
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.Magenta
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { uid = generarUID() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDA0037))
        ) {
            Text("🎲 Generar UID", color = Color.White)
        }

        if (uid.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("🎯 Tu UID: $uid", color = Color.Cyan, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val size = matrixSizeText.toIntOrNull()
                if (uid.isNotEmpty() && size != null && size >= 2) {
                    onStartGame(size, uid)
                }
            },
            enabled = uid.isNotEmpty() && matrixSizeText.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A0572))
        ) {
            Text("🎮 Iniciar Juego", color = Color.White, fontSize = 18.sp)
        }
    }
}

fun generarUID(): String {
    val letras = ('A'..'Z') + ('a'..'z')
    val numeros = ('0'..'9')
    return buildString {
        repeat(2) { append(letras.random()) }
        repeat(2) { append(numeros.random()) }
        append(letras.random())
    }
}
