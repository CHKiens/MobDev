package com.example.mobdev.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepo(application)

    val user: LiveData<FirebaseUser?> = repository.userLiveData
    val errorMessage: LiveData<String?> = repository.errorMessage

    fun loginWithGoogle(idToken: String) {
        repository.loginWithGoogle(idToken)
    }

    fun logOut() {
        repository.logOut()
    }
}

