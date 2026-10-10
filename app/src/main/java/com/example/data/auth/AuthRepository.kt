package com.example.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.BuildConfig
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class AuthUser(
    val uid: String,
    val displayName: String,
    val email: String,
    val emailVerified: Boolean
)

sealed class AuthState {
    object Loading : AuthState()
    object SignedOut : AuthState()
    /** google-services.json belum terpasang → Firebase tidak terinisialisasi. */
    object NotConfigured : AuthState()
    data class SignedIn(val user: AuthUser) : AuthState()
}

/** Hasil operasi autentikasi. [message] selalu aman ditampilkan ke pengguna. */
sealed class AuthResult {
    data class Success(val message: String? = null) : AuthResult()
    data class Failure(val message: String) : AuthResult()
    object Cancelled : AuthResult()
}

/**
 * Autentikasi memakai Firebase Authentication (provider yang sudah dideklarasikan proyek).
 * Kredensial dikelola Firebase; aplikasi tidak menyimpan password atau token secara manual.
 */
class AuthRepository(private val appContext: Context) {

    val isConfigured: Boolean
        get() = FirebaseApp.getApps(appContext).isNotEmpty()

    private val auth: FirebaseAuth? get() = if (isConfigured) FirebaseAuth.getInstance() else null

    val authState: Flow<AuthState> = callbackFlow {
        val fa = auth
        if (fa == null) {
            trySend(AuthState.NotConfigured)
            close()
            return@callbackFlow
        }
        val listener = FirebaseAuth.AuthStateListener { a ->
            trySend(a.currentUser?.let { AuthState.SignedIn(it.toAuthUser()) } ?: AuthState.SignedOut)
        }
        fa.addAuthStateListener(listener)
        awaitClose { fa.removeAuthStateListener(listener) }
    }

    suspend fun signUp(name: String, email: String, password: String): AuthResult {
        val fa = auth ?: return notConfigured()
        return try {
            val user = fa.createUserWithEmailAndPassword(email.trim(), password).await().user
                ?: return AuthResult.Failure("Pendaftaran gagal. Coba lagi.")
            user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name.trim()).build()).await()
            // Pengiriman email verifikasi bersifat best-effort; akun tetap terbuat bila gagal.
            val verifyNote = try {
                user.sendEmailVerification().await()
                "Email verifikasi telah dikirim ke ${email.trim()}."
            } catch (e: Exception) {
                null
            }
            AuthResult.Success(verifyNote)
        } catch (e: Exception) {
            AuthResult.Failure(mapError(e, isSignUp = true))
        }
    }

    suspend fun signIn(email: String, password: String): AuthResult {
        val fa = auth ?: return notConfigured()
        return try {
            fa.signInWithEmailAndPassword(email.trim(), password).await()
            AuthResult.Success()
        } catch (e: Exception) {
            AuthResult.Failure(mapError(e, isSignUp = false))
        }
    }

    /** Selalu menampilkan pesan netral agar tidak membocorkan apakah email terdaftar. */
    suspend fun sendPasswordReset(email: String): AuthResult {
        val fa = auth ?: return notConfigured()
        val neutral = "Jika email terdaftar, tautan untuk mengatur ulang password telah dikirim."
        return try {
            fa.sendPasswordResetEmail(email.trim()).await()
            AuthResult.Success(neutral)
        } catch (e: FirebaseAuthInvalidUserException) {
            AuthResult.Success(neutral)
        } catch (e: Exception) {
            AuthResult.Failure(mapError(e, isSignUp = false))
        }
    }

    suspend fun resendVerification(): AuthResult {
        val user = auth?.currentUser ?: return AuthResult.Failure("Kamu belum masuk.")
        return try {
            user.sendEmailVerification().await()
            AuthResult.Success("Email verifikasi dikirim ulang.")
        } catch (e: Exception) {
            AuthResult.Failure(mapError(e, isSignUp = false))
        }
    }

    /** Login dan pendaftaran Google memakai alur yang sama (Firebase membuat akun jika belum ada). */
    suspend fun signInWithGoogle(activityContext: Context): AuthResult {
        val fa = auth ?: return notConfigured()
        val clientId = googleWebClientId()
            ?: return AuthResult.Failure(
                "Google Sign-In belum dikonfigurasi. Aktifkan provider Google di Firebase dan isi GOOGLE_WEB_CLIENT_ID."
            )
        return try {
            val option = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(clientId)
                .setAutoSelectEnabled(false)
                .build()
            val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
            val result = CredentialManager.create(activityContext).getCredential(activityContext, request)
            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val google = GoogleIdTokenCredential.createFrom(credential.data)
                // Token divalidasi oleh Firebase Auth di server; tidak dikirim ke endpoint buatan sendiri.
                fa.signInWithCredential(GoogleAuthProvider.getCredential(google.idToken, null)).await()
                AuthResult.Success()
            } else {
                AuthResult.Failure("Jenis kredensial Google tidak dikenali.")
            }
        } catch (e: GetCredentialCancellationException) {
            AuthResult.Cancelled
        } catch (e: NoCredentialException) {
            AuthResult.Failure("Tidak ada akun Google di perangkat ini. Tambahkan akun Google lalu coba lagi.")
        } catch (e: GetCredentialException) {
            AuthResult.Failure("Login Google gagal. Coba lagi.")
        } catch (e: GoogleIdTokenParsingException) {
            AuthResult.Failure("Login Google gagal. Coba lagi.")
        } catch (e: Exception) {
            AuthResult.Failure(mapError(e, isSignUp = false))
        }
    }

    suspend fun signOut() {
        auth?.signOut()
        // Hapus status kredensial agar pemilih akun Google tidak memilih otomatis akun sebelumnya.
        try {
            CredentialManager.create(appContext).clearCredentialState(androidx.credentials.ClearCredentialStateRequest())
        } catch (e: Exception) {
            // Best-effort; sesi Firebase sudah berakhir.
        }
    }

    private fun googleWebClientId(): String? {
        BuildConfig.GOOGLE_WEB_CLIENT_ID.takeIf { it.isNotBlank() }?.let { return it }
        // Fallback: string yang dihasilkan plugin google-services dari google-services.json.
        val id = appContext.resources.getIdentifier("default_web_client_id", "string", appContext.packageName)
        return if (id != 0) appContext.getString(id).takeIf { it.isNotBlank() } else null
    }

    private fun notConfigured() = AuthResult.Failure(
        "Autentikasi belum dikonfigurasi: file google-services.json dari Firebase belum terpasang."
    )

    private fun mapError(e: Exception, isSignUp: Boolean): String = when (e) {
        is FirebaseAuthWeakPasswordException -> "Password terlalu lemah. Gunakan kombinasi huruf dan angka yang lebih panjang."
        is FirebaseAuthUserCollisionException ->
            if (isSignUp) "Email ini sudah terdaftar. Coba masuk, atau gunakan 'Lupa password?'."
            else "Email ini terhubung dengan metode login lain. Coba masuk dengan Google."
        is FirebaseAuthInvalidUserException -> {
            val code = e.errorCode
            if (code == "ERROR_USER_DISABLED") "Akun ini dinonaktifkan. Hubungi dukungan." else "Email atau password salah."
        }
        is FirebaseAuthInvalidCredentialsException -> "Email atau password salah."
        is FirebaseTooManyRequestsException -> "Terlalu banyak percobaan. Tunggu beberapa saat lalu coba lagi."
        is FirebaseNetworkException -> "Tidak dapat terhubung ke internet. Periksa koneksimu."
        else -> "Terjadi kesalahan. Coba lagi."
    }

    private fun FirebaseUser.toAuthUser() = AuthUser(
        uid = uid,
        displayName = displayName.orEmpty(),
        email = email.orEmpty(),
        emailVerified = isEmailVerified
    )
}

private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { cont ->
    addOnSuccessListener { cont.resume(it) }
    addOnFailureListener { cont.resumeWithException(it) }
    addOnCanceledListener { cont.cancel() }
}
