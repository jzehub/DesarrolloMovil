package com.example.businesscard
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class EditProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EditProfileScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen() {
    val context = LocalContext.current
    val viewModel: BusinessCardViewModel = viewModel(
        factory = BusinessCardViewModelFactory(context)
    )

    // Valores actuales
    val currentName by viewModel.name.collectAsState()
    val currentRole by viewModel.role.collectAsState()
    val currentYears by viewModel.yearsExperience.collectAsState()
    val currentPhone by viewModel.phone.collectAsState()
    val currentEmail by viewModel.email.collectAsState()

    // Campos editables
    var name by remember { mutableStateOf(currentName) }
    var role by remember { mutableStateOf(currentRole) }
    var years by remember { mutableStateOf(currentYears.toString()) }
    var phone by remember { mutableStateOf(currentPhone) }
    var email by remember { mutableStateOf(currentEmail) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Editar Perfil") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
            OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Cargo") })
            OutlinedTextField(value = years, onValueChange = { years = it }, label = { Text("Años de experiencia") })
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Teléfono") })
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo") })

            Button(
                onClick = {
                    val yearsInt = years.toIntOrNull() ?: 0
                    viewModel.save(name, role, yearsInt, phone, email)
                    (context as? ComponentActivity)?.finish()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        }
    }
}
