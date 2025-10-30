package com.example.mobdev.model

data class SalesItem(
    val id: Int = 0,
    val description: String = "",
    val price: Double = 0.0,
    val sellerEmail: String? = null,
    val sellerPhone: String? = null,
    val pictureUrl: String? = null,
    val userId: String? = null,
    val time: Int = 0)

