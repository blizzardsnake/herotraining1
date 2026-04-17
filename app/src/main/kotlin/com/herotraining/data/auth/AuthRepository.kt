package com.herotraining.data.auth

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.herotraining.data.repo.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

/** Web client id from google-services.json (client_type = 3, used to request ID token). */
private const val WEB_CLIENT_ID = "490017466168-q2lh3d6ulga7vuburldkptcqrsllfuai.apps.googleusercontent.com"

sealed interface AuthStatus {
    data object Loading : AuthStatus
    data object SignedOut : AuthStatus
    data class SignedIn(val uid: String, val email: String?, val name: String?, val photoUrl: String?) : AuthStatus
}

class AuthRepository(
    private val context: Context,
    private val profileRepo: ProfileRepository
) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _status = MutableStateFlow<AuthStatus>(AuthStatus.Loading)
    val status: StateFlow<AuthStatus> = _status.asStateFlow()

    val signInClient: GoogleSignInClient = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .requestProfile()
            .build()
    )

    init {
        refreshCurrentUser()
    }

    fun refreshCurrentUser() {
        val u = auth.currentUser
        _status.value = if (u != null) u.toSignedIn() else AuthStatus.SignedOut
    }

    fun createSignInIntent(): Intent = signInClient.signInIntent

    /** Call from ActivityResult callback with the Intent returned by sign-in. */
    suspend fun handleSignInResult(result: ActivityResult): Result<AuthStatus.SignedIn> = runCatching {
        val account = GoogleSignIn.getSignedInAccountFromIntent(result.data).await()
        val idToken = account.idToken ?: error("No idToken from Google")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val firebaseUser = auth.signInWithCredential(credential).await().user
            ?: error("Firebase sign-in returned null user")
        val signedIn = firebaseUser.toSignedIn()
        _status.value = signedIn
        profileRepo.setAuth(
            uid = signedIn.uid,
            email = signedIn.email,
            name = signedIn.name,
            photo = signedIn.photoUrl
        )
        signedIn
    }

    suspend fun signOut() {
        auth.signOut()
        signInClient.signOut().await()
        profileRepo.clearAuth()
        _status.value = AuthStatus.SignedOut
    }

    private fun FirebaseUser.toSignedIn() = AuthStatus.SignedIn(
        uid = uid,
        email = email,
        name = displayName,
        photoUrl = photoUrl?.toString()
    )
}
