package com.example.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.ui.theme.PresentationTheme

class BotCardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PresentationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF5F5F5) // Fondo claro
                ) {
                    BotProfileCard()
                }
            }
        }
    }
}

@Composable
fun BotProfileCard() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // Imagen del bot
        Image(
            painter = painterResource(id = R.drawable.androidbotpedro),
            contentDescription = "Avatar del bot",
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(100.dp))
        )

        Text(
            "Pedro Lucero",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A237E)
        )
        Text(
            "Ingeniero de Software",
            fontSize = 16.sp,
            color = Color(0xFF607D8B)
        )

        Divider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = Color(0xFFBDBDBD)
        )

        // Datos de contacto
        ContactRow(Icons.Default.Email, "pedroalucero@gmail.com")
        ContactRow(Icons.Default.Phone, "(+507)6997-2738")
        ContactRow(Icons.Default.Info, "linkedin.com/in/pedroalucero")

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Habilidades",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A237E)
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SkillTag("Data Science")
            SkillTag("Python")
            SkillTag("SQL")
            SkillTag("Machine Learning")
        }
    }
}

@Composable
fun ContactRow(icon: ImageVector, data: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFF0288D1),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = data, color = Color(0xFF424242), fontSize = 15.sp)
    }
}

@Composable
fun SkillTag(text: String) {
    Surface(
        color = Color(0xFFE3F2FD),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFF1565C0),
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}
