package com.example.bingo_app

import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

data class Celda(val numero: Int, var marcada: Boolean = false)

@Composable
fun Activity2(size: Int, uid: String) {
    val context = LocalContext.current
    var matriz by remember { mutableStateOf(generarMatriz(size)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B1B2F))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🎯 UID: $uid", color = Color.Yellow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Selecciona celdas para marcar",
            color = Color.White,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        for (i in 0 until size) {
            Row {
                for (j in 0 until size) {
                    val celda = matriz[i][j]
                    val colorFondo = if (celda.marcada) Color(0xFF00FF88) else Color(0xFF2C2C54)

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(colorFondo)
                            .border(
                                width = 2.dp,
                                brush = Brush.linearGradient(
                                    listOf(Color.Magenta, Color.Cyan, Color.Yellow)
                                ),
                                shape = CircleShape
                            )
                            .clickable(enabled = !celda.marcada) {
                                celda.marcada = true
                                matriz = matriz.map { it.copyOf() }.toTypedArray()

                                if (hayConsecutivosMarcados(matriz)) {
                                    Toast.makeText(context, "¡Bingo!", Toast.LENGTH_LONG).show()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = celda.numero.toString(),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                matriz = generarMatriz(size)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A0572))
        ) {
            Text("🔁 Regenerar Carta", color = Color.White)
        }
    }
}


// 🎯 Función para generar la carta
fun generarMatriz(size: Int): Array<Array<Celda>> {
    val total = size * size
    val numeros = (1..(total * 2)).shuffled().take(total)
    val matriz = Array(size) { Array(size) { Celda(0) } }
    var index = 0
    for (i in 0 until size) {
        for (j in 0 until size) {
            matriz[i][j] = Celda(numeros[index++])
        }
    }
    return matriz
}

// ✅ Detectar 5 marcas consecutivas en cualquier dirección
fun hayConsecutivosMarcados(matriz: Array<Array<Celda>>): Boolean {
    val n = matriz.size
    val target = n  // Ahora se requiere n consecutivos

    // Filas
    for (i in 0 until n) {
        var count = 0
        for (j in 0 until n) {
            count = if (matriz[i][j].marcada) count + 1 else 0
            if (count == target) return true
        }
    }

    // Columnas
    for (j in 0 until n) {
        var count = 0
        for (i in 0 until n) {
            count = if (matriz[i][j].marcada) count + 1 else 0
            if (count == target) return true
        }
    }

    // Diagonal principal (↘)
    var countDiag1 = 0
    for (i in 0 until n) {
        countDiag1 = if (matriz[i][i].marcada) countDiag1 + 1 else 0
        if (countDiag1 == target) return true
    }

    // Diagonal secundaria (↙)
    var countDiag2 = 0
    for (i in 0 until n) {
        countDiag2 = if (matriz[i][n - 1 - i].marcada) countDiag2 + 1 else 0
        if (countDiag2 == target) return true
    }

    return false
}

