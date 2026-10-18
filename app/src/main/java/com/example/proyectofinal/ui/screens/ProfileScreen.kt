package com.example.proyectofinal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.ui.model.AuthUiState

@Composable
fun ProfileScreen(
    uiState: AuthUiState,
    onLogout: () -> Unit
) {
    val user = uiState.user

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Perfil de Usuario",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Nombre:",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = user?.displayName ?: "No disponible"
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = "Correo:",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = user?.email ?: "No disponible"
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = "UID:",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = user?.uid ?: "No disponible"
                )

            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = {
                onLogout()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Cerrar sesión"
            )
        }

        StatusMessage(
            uiState = uiState
        )
    }
}
