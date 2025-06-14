package com.example.talkingappv20

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.compose.ui.graphics.Color
import android.speech.tts.Voice
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.talkingappv20.ui.theme.TalkingAppV20Theme
import java.util.*

class MainActivity : ComponentActivity() {
    val color1 = Color(0xFFDAD7CD)
    val color2 = Color(0xFFA3B18A)
    val color3 = Color(0xFF588157)
    val color4 = Color(0xFF3A5A40)
    val color5= Color(0xFF344E41)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TalkingAppV20Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    Box(modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(color1)) {
                        TextToSpeechScreen()
                    }
                }
            }
        }
    }
}
@Composable
fun TextToSpeechScreen() {
    val color1 = Color(0xFFDAD7CD)
    val color2 = Color(0xFFA3B18A)
    val color3 = Color(0xFF588157)
    val color4 = Color(0xFF3A5A40)
    val color5= Color(0xFF344E41)

    val context = LocalContext.current
    val ttsSpanish = remember { mutableStateOf<TextToSpeech?>(null) }
    val ttsEnglish = remember { mutableStateOf<TextToSpeech?>(null) }

    var customText by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf("en") }
    var selectedVoice by remember { mutableStateOf("en-US") }

    // Inicializar motores TTS una vez
    LaunchedEffect(Unit) {
        ttsSpanish.value = TextToSpeech(context) {
            if (it == TextToSpeech.SUCCESS) {
                ttsSpanish.value?.language = Locale("es", "ES")
            }
        }
        ttsEnglish.value = TextToSpeech(context) {
            if (it == TextToSpeech.SUCCESS) {
                ttsEnglish.value?.language = Locale("en", "US")
            }
        }
    }


    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(32.dp))

        // Sección personalizada
        Text("Texto Personalizado", style = MaterialTheme.typography.titleMedium, color = color4)
        OutlinedTextField(
            value = customText,
            onValueChange = { customText = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            placeholder = { Text("Escribe algo...") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = color4,
                unfocusedTextColor = color4
            )
        )

        Text("Idioma:", color = color4)
        Row(modifier = Modifier.padding(vertical = 4.dp)) {
            Button(
                onClick = {
                    selectedLanguage = "es"
                    selectedVoice = "es-MX"
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedLanguage == "es") color4 else color3,
                    contentColor = if (selectedLanguage == "es") color4 else color3
                ),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text("Español", color = color2)
            }

            Button(
                onClick = {
                    selectedLanguage = "en"
                    selectedVoice = "en-US"
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedLanguage == "en") color4 else color3,
                    contentColor = if (selectedLanguage == "en") color4 else color3
                )
            ) {
                Text("Inglés", color = color1)
            }
        }

        Text("Acento:", color = color4)
        Row(modifier = Modifier.padding(vertical = 4.dp)) {
            val accentOptions = when (selectedLanguage) {
                "es" -> listOf("es-MX", "es-ES")
                "en" -> listOf("en-US", "en-GB")
                else -> listOf("default")
            }

            accentOptions.forEach { accent ->
                Button(
                    onClick = { selectedVoice = accent },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedVoice == accent) color4 else color3,
                        contentColor = if (selectedVoice == accent) color4 else color3
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(accent, color = color1)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                val voice = Voice(
                    selectedVoice,
                    Locale.forLanguageTag(selectedVoice),
                    Voice.QUALITY_NORMAL,
                    Voice.LATENCY_NORMAL,
                    false,
                    null
                )
                val engine = if (selectedLanguage == "es") ttsSpanish.value else ttsEnglish.value
                engine?.voice = voice
                engine?.speak(customText, TextToSpeech.QUEUE_FLUSH, null, null)
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(color4),
            colors = ButtonDefaults.buttonColors(
                containerColor = color4,
                contentColor = color2
            )
        ) {
            Text("🔊 Reproducir Texto")
        }
    }
}
