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
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }



    DisposableEffect(Unit) {
        onDispose {
            tts?.shutdown()
        }
    }

    var matriz by remember { mutableStateOf(generarMatriz(size)) }
    val numerosSorteados = remember { mutableStateListOf<Int>() }
    var numeroActual by remember { mutableStateOf<Int?>(null) }

    // Animación para el número actual
    val infiniteTransition = rememberInfiniteTransition()
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B1B2F))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🎯 UID: $uid", color = Color.Yellow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("🎲 Ronda: ${numerosSorteados.size + 1}", color = Color.White, fontSize = 18.sp)
        Text(
            "🎯 Número actual: ${numeroActual ?: "-"}",
            color = Color.Cyan.copy(alpha = animatedAlpha),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
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
                            .clickable(
                                enabled = !celda.marcada && numeroActual != null && celda.numero == numeroActual
                            ) {
                                if (celda.numero == numeroActual) {
                                    celda.marcada = true
                                    matriz = matriz.map { it.copyOf() }.toTypedArray()

                                    if (hayBingo(matriz)) {
                                        Toast
                                            .makeText(context, "¡Bingo!", Toast.LENGTH_LONG)
                                            .show()
                                        tts?.speak("¡Bingo!", TextToSpeech.QUEUE_FLUSH, null, null)
                                    }
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
                val total = size * size * 2
                val restantes = (1..total).filter { it !in numerosSorteados }

                if (restantes.isNotEmpty()) {
                    val nuevo = restantes.random()
                    numeroActual = nuevo
                    numerosSorteados.add(nuevo)
                    tts?.speak("Número $nuevo", TextToSpeech.QUEUE_FLUSH, null, null)
                } else {
                    Toast.makeText(context, "Ya no hay más números", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDA0037))
        ) {
            Text("🎱 Sacar número", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                matriz = generarMatriz(size)
                numerosSorteados.clear()
                numeroActual = null
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A0572))
        ) {
            Text("🔁 Regenerar Carta", color = Color.White)
        }
    }
}

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

fun hayBingo(matriz: Array<Array<Celda>>): Boolean {
    val n = matriz.size
    for (i in 0 until n) {
        if (matriz[i].all { it.marcada }) return true
    }
    for (j in 0 until n) {
        if ((0 until n).all { i -> matriz[i][j].marcada }) return true
    }
    if ((0 until n).all { i -> matriz[i][i].marcada }) return true
    if ((0 until n).all { i -> matriz[i][n - 1 - i].marcada }) return true
    return false
}
