package com.example.proyectofinal
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.proyectofinal.data.DatabaseHelper
import com.example.proyectofinal.data.Sincronizador
import com.example.proyectofinal.ui.navigation.AppNavigation
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme
import com.example.proyectofinal.viewmodel.AuthViewModel
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

class MainActivity : ComponentActivity() {

    private lateinit var callbackManager: CallbackManager
    private lateinit var authViewModel: AuthViewModel
    private lateinit var dbHelper: DatabaseHelper

    // Permiso para mostrar notificaciones de reserva (Android 13 o superior).
    private val permisoNotificaciones =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        callbackManager =
            CallbackManager.Factory.create()

        authViewModel =
            ViewModelProvider(this)[AuthViewModel::class.java]

        dbHelper = DatabaseHelper(applicationContext)

        // Descarga el catálogo publicado desde el portal administrativo (si hay servidor).
        Sincronizador.descargarCatalogo(dbHelper)

        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        LoginManager
            .getInstance()
            .registerCallback(
                callbackManager,
                object : FacebookCallback<LoginResult> {

                    override fun onSuccess(
                        result: LoginResult
                    ) {
                        authViewModel.loginFacebook(
                            result.accessToken
                        ) {
                            /*
                             El cambio de estado se realiza en el ViewModel.
                             La navegación visual se controla desde Compose.
                            */
                        }
                    }

                    override fun onCancel() {
                        authViewModel.setMessage(
                            "Login con Facebook cancelado"
                        )
                    }

                    override fun onError(
                        error: FacebookException
                    ) {
                        authViewModel.setMessage(
                            error.message ?: "Error en Facebook Login"
                        )
                    }
                }
            )
        setContent {
            ProyectoFinalTheme {
                AppNavigation(
                    authViewModel = authViewModel,
                    dbHelper = dbHelper,
                    onFacebookLogin = {

                        LoginManager
                            .getInstance()
                            .logInWithReadPermissions(
                                this,
                                listOf(
                                    "public_profile"
                                )
                            )
                    }
                )
            }
        }

    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(
            requestCode,
            resultCode,
            data
        )
    }
}
