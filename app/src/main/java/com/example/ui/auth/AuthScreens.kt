package com.example.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.BlueSoftBg
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.SuccessContainer
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

private enum class AuthMode { LOGIN, SIGNUP, FORGOT }

@Composable
fun AuthFlow(viewModel: AuthViewModel, modifier: Modifier = Modifier) {
    val form by viewModel.form.collectAsStateWithLifecycle()
    var mode by rememberSaveable { mutableStateOf(AuthMode.LOGIN) }
    val context = LocalContext.current

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }

    fun switchTo(m: AuthMode) { viewModel.clearMessages(); password = ""; confirm = ""; mode = m }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BlueSoftBg)
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("auth_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Text("NaikLevel", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = BluePrimary))
        Text(
            when (mode) {
                AuthMode.LOGIN -> "Masuk ke akunmu"
                AuthMode.SIGNUP -> "Buat akun baru"
                AuthMode.FORGOT -> "Atur ulang password"
            },
            style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold)
        )

        if (mode == AuthMode.SIGNUP) {
            OutlinedTextField(
                value = name, onValueChange = { name = it.take(60); viewModel.clearMessages() },
                label = { Text("Nama lengkap") }, singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("auth_name")
            )
        }
        OutlinedTextField(
            value = email, onValueChange = { email = it.take(254); viewModel.clearMessages() },
            label = { Text("Email") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().testTag("auth_email")
        )
        if (mode != AuthMode.FORGOT) {
            OutlinedTextField(
                value = password, onValueChange = { password = it.take(128); viewModel.clearMessages() },
                label = { Text("Password") }, singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showPassword) "Sembunyikan password" else "Tampilkan password"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("auth_password")
            )
        }
        if (mode == AuthMode.SIGNUP) {
            OutlinedTextField(
                value = confirm, onValueChange = { confirm = it.take(128); viewModel.clearMessages() },
                label = { Text("Konfirmasi password") }, singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth().testTag("auth_confirm")
            )
            Text("Minimal 8 karakter, mengandung huruf dan angka.", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
        }

        form.error?.let { MessageBox(it, isError = true) }
        form.info?.let { MessageBox(it, isError = false) }

        Button(
            onClick = {
                when (mode) {
                    AuthMode.LOGIN -> viewModel.signIn(email, password)
                    AuthMode.SIGNUP -> viewModel.signUp(name, email, password, confirm)
                    AuthMode.FORGOT -> viewModel.resetPassword(email)
                }
            },
            enabled = !form.isLoading,
            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("auth_submit")
        ) {
            if (form.isLoading) CircularProgressIndicator(Modifier.height(20.dp), strokeWidth = 2.dp, color = SurfaceWhite)
            else Text(when (mode) { AuthMode.LOGIN -> "Masuk"; AuthMode.SIGNUP -> "Buat Akun"; AuthMode.FORGOT -> "Kirim tautan" })
        }

        if (mode != AuthMode.FORGOT) {
            OutlinedButton(
                onClick = { viewModel.signInWithGoogle(context) },
                enabled = !form.isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("auth_google")
            ) { Text(if (mode == AuthMode.LOGIN) "Lanjutkan dengan Google" else "Daftar dengan Google") }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            when (mode) {
                AuthMode.LOGIN -> {
                    TextButton(onClick = { switchTo(AuthMode.FORGOT) }) { Text("Lupa password?") }
                    TextButton(onClick = { switchTo(AuthMode.SIGNUP) }) { Text("Belum punya akun? Daftar") }
                }
                AuthMode.SIGNUP -> TextButton(onClick = { switchTo(AuthMode.LOGIN) }) { Text("Sudah punya akun? Masuk") }
                AuthMode.FORGOT -> TextButton(onClick = { switchTo(AuthMode.LOGIN) }) { Text("Kembali ke halaman masuk") }
            }
        }
    }
}

@Composable
private fun MessageBox(text: String, isError: Boolean) {
    androidx.compose.material3.Surface(
        color = if (isError) ErrorContainer else SuccessContainer,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text, modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodySmall.copy(color = if (isError) ErrorRed else SuccessGreen)
        )
    }
}

@Composable
fun AuthLoadingScreen() {
    androidx.compose.foundation.layout.Box(Modifier.fillMaxSize().background(BlueSoftBg), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun AuthNotConfiguredScreen() {
    Column(Modifier.fillMaxSize().background(BlueSoftBg).safeDrawingPadding().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("Autentikasi belum dikonfigurasi", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
        Spacer(Modifier.height(8.dp))
        Text(
            "Tambahkan google-services.json dari proyek Firebase ke folder app/, aktifkan Email/Password dan Google di Firebase Authentication, lalu bangun ulang aplikasi.",
            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
        )
    }
}
