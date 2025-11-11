package com.example.mobdev.auth

class FakeAuthViewModel {
    var userEmail: String? = null
    fun login(email: String, password: String) {
        userEmail = email
    }
    fun register(email: String, password: String) {
        userEmail = email
    }
}