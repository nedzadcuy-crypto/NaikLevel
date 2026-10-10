package com.example.ui.auth

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthRepository
import com.example.data.auth.AuthResult
import com.example.data.auth.AuthState
import com.example.data.auth.AuthValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AuthFormState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val info: String? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(application.applicationContext)

    val authState: StateFlow<AuthState> = repository.authState
        .stateIn(viewModelScope, SharingStarted.Eagerly, AuthState.Loading)

    private val _form = MutableStateFlow(AuthFormState())
    val form: StateFlow<AuthFormState> = _form.asStateFlow()

    fun clearMessages() { _form.value = AuthFormState(isLoading = _form.value.isLoading) }

    fun signIn(email: String, password: String) {
        val err = AuthValidator.validateEmail(email) ?: AuthValidator.validateLoginPassword(password)
        if (err != null) { _form.value = AuthFormState(error = err); return }
        run { repository.signIn(email, password) }
    }

    fun signUp(name: String, email: String, password: String, confirm: String) {
        val err = AuthValidator.validateName(name)
            ?: AuthValidator.validateEmail(email)
            ?: AuthValidator.validateNewPassword(password)
            ?: AuthValidator.validateConfirm(password, confirm)
        if (err != null) { _form.value = AuthFormState(error = err); return }
        run { repository.signUp(name, email, password) }
    }

    fun resetPassword(email: String) {
        val err = AuthValidator.validateEmail(email)
        if (err != null) { _form.value = AuthFormState(error = err); return }
        run { repository.sendPasswordReset(email) }
    }

    fun signInWithGoogle(activityContext: Context) {
        run { repository.signInWithGoogle(activityContext) }
    }

    fun resendVerification() { run { repository.resendVerification() } }

    fun signOut() {
        _form.value = AuthFormState()
        viewModelScope.launch { repository.signOut() }
    }

    private fun run(block: suspend () -> AuthResult) {
        if (_form.value.isLoading) return
        _form.value = AuthFormState(isLoading = true)
        viewModelScope.launch {
            _form.value = when (val r = block()) {
                is AuthResult.Success -> AuthFormState(info = r.message)
                is AuthResult.Failure -> AuthFormState(error = r.message)
                AuthResult.Cancelled -> AuthFormState(info = "Login dibatalkan.")
            }
        }
    }
}
