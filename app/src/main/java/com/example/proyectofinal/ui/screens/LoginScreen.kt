
package com.example.proyectofinal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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

private val AzulTripGo = Color(0xFF1769FF)
private val FondoTripGo = Color(0xFFF5F8FF)
private val TextoTripGo = Color(0xFF172B4D)
private val AzulFacebook = Color(0xFF1877F2)

@Composable
fun LoginScreen(
    uiState: AuthUiState,
    onLogin: (String, String) -> Unit,
    onFacebookLogin: () -> Unit,
    onGoRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoTripGo)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Identidad de la aplicación
        Text(
            text = "✈ TripGo",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold,
            color = AzulTripGo
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tu próxima aventura comienza aquí",
            style = MaterialTheme.typography.bodyLarge,
            color = TextoTripGo
        )

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "¡Bienvenido!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextoTripGo
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Inicia sesión para descubrir nuevos destinos.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(28.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            placeholder = { Text("ejemplo@correo.com") },
            singleLine = true,
            enabled = !uiState.loading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            enabled = !uiState.loading,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onLogin(email.trim(), password) },
            enabled = !uiState.loading &&
                    email.isNotBlank() &&
                    password.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AzulTripGo
            )
        ) {
            Text(
                text = "Ingresar",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedButton(
            onClick = onFacebookLogin,
            enabled = !uiState.loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = AzulFacebook,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "f  Continuar con Facebook",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        TextButton(
            onClick = onGoRegister,
            enabled = !uiState.loading
        ) {
            Text(
                text = "¿No tienes cuenta? Crear cuenta",
                color = AzulTripGo
            )
        }

        StatusMessage(uiState = uiState)

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun StatusMessage(uiState: AuthUiState) {
    if (uiState.loading) {
        Spacer(modifier = Modifier.height(16.dp))

        CircularProgressIndicator(
            color = AzulTripGo
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Conectando...",
            color = TextoTripGo,
            style = MaterialTheme.typography.bodyMedium
        )
    }

    if (uiState.message.isNotBlank()) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = uiState.message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
