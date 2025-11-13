package com.example.mobdev.model

data class SalesItem(
    val id: Int = 0,
    val description: String = "",
    val price: Int = 0,
    val sellerEmail: String = "",
    val sellerPhone: String = "",
    val time: Long = 0,
    val pictureUrl: String = ""
)
