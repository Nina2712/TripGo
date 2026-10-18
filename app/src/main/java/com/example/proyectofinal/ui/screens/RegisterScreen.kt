
package com.example.proyectofinal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.ui.model.AuthUiState

private val AzulRegistro = Color(0xFF1769FF)
private val FondoRegistro = Color(0xFFF5F8FF)
private val TextoRegistro = Color(0xFF172B4D)

@Composable
fun RegisterScreen(
    uiState: AuthUiState,
    onRegister: (String, String) -> Unit,
    onGoLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var verClave by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoRegistro)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Logo y nombre de la aplicación
        Text(
            text = "✈ TripGo",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold,
            color = AzulRegistro
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "El mundo te espera",
            style = MaterialTheme.typography.bodyLarge,
            color = TextoRegistro
        )

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "¡Únete a TripGo!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextoRegistro
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Crea tu cuenta y empieza a planear\n"
                    + "tu próxima aventura.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Correo electrónico
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            placeholder = { Text("ejemplo@correo.com") },
            singleLine = true,
            enabled = !uiState.loading,
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = AzulRegistro) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            placeholder = { Text("Mínimo 6 caracteres") },
            singleLine = true,
            enabled = !uiState.loading,
            visualTransformation = if (verClave) VisualTransformation.None
            else PasswordVisualTransformation(),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AzulRegistro) },
            trailingIcon = {
                TextButton(onClick = { verClave = !verClave }) {
                    Text(if (verClave) "Ocultar" else "Mostrar", color = AzulRegistro)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(26.dp))

        // Botón para crear la cuenta
        Button(
            onClick = {
                onRegister(email.trim(), password)
            },
            enabled = !uiState.loading &&
                    email.isNotBlank() &&
                    password.length >= 6,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AzulRegistro
            )
        ) {
            Text(
                text = "Crear mi cuenta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        TextButton(
            onClick = onGoLogin,
            enabled = !uiState.loading
        ) {
            Text(
                text = "¿Ya tienes una cuenta? Inicia sesión",
                color = AzulRegistro
            )
        }

        // Estado de autenticación
        if (uiState.loading) {
            Spacer(modifier = Modifier.height(12.dp))

            CircularProgressIndicator(
                color = AzulRegistro
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Creando tu cuenta...",
                color = TextoRegistro
            )
        }

        if (uiState.message.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = uiState.message,
                color = Color(0xFFC53030),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFDECEC), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
