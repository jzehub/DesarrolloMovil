package com.example.bingo_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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

    val fondo = Brush.verticalGradient(
        listOf(Color(0xFF0F0C29), Color(0xFF302B63), Color(0xFF24243E))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondo)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "💥 BINGO EXPLOSIÓN 💥",
            color = Color(0xFFFFD700),
            fontSize = 34.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.shadow(10.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = matrixSizeText,
            onValueChange = { matrixSizeText = it },
            label = { Text("🎯 Tamaño de la carta", color = Color.White) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color.Magenta, RoundedCornerShape(12.dp)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Cyan,
                unfocusedBorderColor = Color.LightGray,
                cursorColor = Color.Magenta
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { uid = generarUID() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFC466B)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("🎲 Generar UID", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        if (uid.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text("🎰 Tu UID: $uid", color = Color.Cyan, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
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
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF24C6DC))
        ) {
            Text("🎮 ¡Apostar al Bingo!", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
