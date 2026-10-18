package com.example.proyectofinal.data

import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun register(
        email: String,
        password: String,
        onResult: (Boolean, String, FirebaseUser?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener { task ->

            if (task.isSuccessful) {

                onResult(
                    true,
                    "Usuario registrado correctamente",
                    auth.currentUser
                )

            } else {

                onResult(
                    false,
                    task.exception?.message ?: "Error al registrar usuario",
                    null
                )

            }

        }
    }

    fun login(
        email: String,
        password: String,
        onResult: (Boolean, String, FirebaseUser?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener { task ->

            if (task.isSuccessful) {

                onResult(
                    true,
                    "Inicio de sesión correcto",
                    auth.currentUser
                )

            } else {

                onResult(
                    false,
                    task.exception?.message ?: "Error al iniciar sesión",
                    null
                )

            }

        }
    }

    fun loginWithFacebook(
        token: AccessToken,
        onResult: (Boolean, String, FirebaseUser?) -> Unit
    ) {
        val credential =
            FacebookAuthProvider.getCredential(
                token.token
            )

        auth.signInWithCredential(
            credential
        ).addOnCompleteListener { task ->

            if (task.isSuccessful) {

                onResult(
                    true,
                    "Inicio de sesión con Facebook correcto",
                    auth.currentUser
                )

            } else {

                onResult(
                    false,
                    task.exception?.message ?: "Error con Facebook Login",
                    null
                )

            }

        }
    }

    fun logout() {
        auth.signOut()

        LoginManager
            .getInstance()
            .logOut()
    }
}
