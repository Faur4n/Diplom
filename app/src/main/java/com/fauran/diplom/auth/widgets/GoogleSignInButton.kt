package com.fauran.diplom.auth.widgets

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fauran.diplom.GOOGLE_SIGN_IN
import com.fauran.diplom.R
import com.fauran.diplom.TAG
import com.fauran.diplom.ui.theme.googleText
import com.fauran.diplom.util.GoogleSignInContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


@Composable
fun GoogleSignInButton(
    modifier: Modifier,
    onSuccess: () -> Unit,
    onError: () -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(GoogleSignInContract(context)) { account ->
        Log.d(TAG, "GoogleSignIn: $account")
        val token = account?.idToken
        when {
            account == null || token == null -> {
                onError()
            }
            else -> {
                val credential = GoogleAuthProvider.getCredential(token, null)
                FirebaseAuth.getInstance().signInWithCredential(credential).addOnSuccessListener {
                    onSuccess()
                }.addOnFailureListener {
                    onError()
                }
            }
        }
    }
    Card(
        modifier = modifier
            .clickable {
                launcher.launch(GOOGLE_SIGN_IN)
            }) {
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "sign in with google",
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = "SIGN IN WITH GOOGLE",
                style = googleText,
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp)
                    .align(Alignment.CenterVertically)

            )
        }
    }
}