package com.example.mobdev.repository

import com.example.mobdev.model.SalesItem
import retrofit2.Call


import retrofit2.http.*

interface SalesItemService {
    @GET("SalesItems")
    fun getAllSalesItems(): Call<List<SalesItem>>

    @GET("SalesItems/{id}")
    fun getSalesItemById(@Path("id") id: Int): Call<SalesItem>

    @POST("SalesItems")
    fun createSalesItem(@Body item: SalesItem): Call<SalesItem>

    @DELETE("SalesItems/{id}")
    fun deleteSalesItem(@Path("id") id: Int): Call<SalesItem>
}