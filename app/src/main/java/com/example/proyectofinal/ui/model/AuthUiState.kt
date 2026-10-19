package com.example.proyectofinal.ui.model

import com.google.firebase.auth.FirebaseUser

data class AuthUiState(
    val user: FirebaseUser? = null,
    val message: String = "",
    val loading: Boolean = false
)
