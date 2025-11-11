package com.example.mobdev.auth

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class AuthRepo(application: Application) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _userLiveData = MutableLiveData<FirebaseUser?>(firebaseAuth.currentUser)
    val userLiveData: LiveData<FirebaseUser?> get() = _userLiveData

    private val _loggedOutLiveData = MutableLiveData<Boolean>(firebaseAuth.currentUser == null)
    val loggedOutLiveData: LiveData<Boolean> get() = _loggedOutLiveData

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun loginWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _userLiveData.postValue(firebaseAuth.currentUser)
                    _loggedOutLiveData.postValue(false)
                    _errorMessage.postValue(null)
                } else {
                    _errorMessage.postValue(task.exception?.message ?: "Unknown login error")
                }
            }
    }

    fun logOut() {
        firebaseAuth.signOut()
        _userLiveData.postValue(null)
        _loggedOutLiveData.postValue(true)
    }

}
