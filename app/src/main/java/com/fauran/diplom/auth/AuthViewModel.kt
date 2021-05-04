package com.fauran.diplom.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.registerForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException


@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {



}
