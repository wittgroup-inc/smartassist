package com.gowittgroup.smartassist.ui.auth.signin.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.gowittgroup.core.logger.SmartLog
import com.gowittgroup.smartassist.R
import com.gowittgroup.smartassist.ui.theme.SmartAssistTheme

@Composable
fun GoogleSignInButton(
    onTokenReceived: (String) -> Unit
) {
    val context = LocalContext.current
    val signInClient = remember { Identity.getSignInClient(context) }
    var launchSignIn by remember { mutableStateOf(false) }
    var intentSenderRequest: IntentSenderRequest? by remember { mutableStateOf(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            handleSignInResult(result, signInClient, onTokenReceived, onError)
            launchSignIn = false
        }
    )

    LaunchedEffect(key1 = launchSignIn) {
        if (launchSignIn) {
            intentSenderRequest?.let {
                launcher.launch(it)
            }
        }
    }


    Button(
        onClick = {
            val signInRequest = buildSignInRequest(context)
            signInClient.beginSignIn(signInRequest)
                .addOnSuccessListener { result ->
                    intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    launchSignIn = true
                }
                .addOnFailureListener { e ->
                    SmartLog.e("GoogleSignIn", "Sign-in failed", e)
                    onError("Sign-in failed: ${e.message}")
                }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.google),
                contentDescription = "Google Sign-In",
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp)
            )
            Text(
                modifier = Modifier.padding(vertical = 6.dp),
                text = stringResource(R.string.sign_in_with_google),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }

}

val onError: (String) -> Unit = { errorMessage ->
    SmartLog.d("Google Login","An error occurred: $errorMessage")
}

private fun handleSignInResult(
    result: ActivityResult,
    signInClient: SignInClient,
    onTokenReceived: (String) -> Unit,
    onError: (String) -> Unit
) {
    if (result.resultCode == Activity.RESULT_OK) {
        try {
            val credential = signInClient.getSignInCredentialFromIntent(result.data ?: Intent())
            val idToken = credential.googleIdToken
            if (idToken != null) {
                onTokenReceived(idToken)
            } else {
                onError("Google ID token is null")
            }
        } catch (e: ApiException) {
            SmartLog.e("GoogleSignIn", "Sign-in failed", e)
            onError("Sign-in failed: ${e.message}")
        }
    } else {
        onError("Sign-in cancelled or failed")
    }
}

private fun buildSignInRequest(context: Context): BeginSignInRequest {
    val serverClientId = getServerClientId(context)
    return BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(serverClientId)
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .setAutoSelectEnabled(true)
        .build()
}

private fun getServerClientId(context: Context): String {
    return context.getString(R.string.default_web_client_id)
}

@Preview
@Composable
private fun GoogleLoginButtonPrev() {
    SmartAssistTheme {
        GoogleSignInButton(onTokenReceived = {})
    }
}