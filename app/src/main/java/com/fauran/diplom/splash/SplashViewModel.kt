package com.fauran.diplom.splash

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel(){

    suspend fun userIsAuthorized(
    ) : Boolean{
//        return false
        return FirebaseAuth.getInstance().currentUser != null
    }
}