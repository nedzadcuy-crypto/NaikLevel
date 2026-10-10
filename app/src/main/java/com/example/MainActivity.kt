package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.auth.AuthState
import com.example.ui.NaikLevelApp
import com.example.ui.auth.AuthFlow
import com.example.ui.auth.AuthLoadingScreen
import com.example.ui.auth.AuthNotConfiguredScreen
import com.example.ui.auth.AuthViewModel
import com.example.ui.theme.NaikLevelTheme

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NaikLevelTheme {
                val authState by authViewModel.authState.collectAsStateWithLifecycle()
                when (val s = authState) {
                    AuthState.Loading -> AuthLoadingScreen()
                    AuthState.NotConfigured -> AuthNotConfiguredScreen()
                    AuthState.SignedOut -> AuthFlow(authViewModel)
                    is AuthState.SignedIn -> NaikLevelApp(
                        accountUid = s.user.uid,
                        accountName = s.user.displayName,
                        accountEmail = s.user.email,
                        emailVerified = s.user.emailVerified,
                        onSignOut = { authViewModel.signOut() },
                        onResendVerification = { authViewModel.resendVerification() }
                    )
                }
            }
        }
    }
}
