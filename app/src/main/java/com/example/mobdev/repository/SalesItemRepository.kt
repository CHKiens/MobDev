package com.example.mobdev.repository

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.mobdev.model.SalesItem
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SalesItemRepository {
    private val baseUrl = "https://anbo-salesitems.azurewebsites.net/api/"
    private val tag = "SalesItemRepository"
    private val salesItemService: SalesItemService
    val salesItems: MutableState<List<SalesItem>> = mutableStateOf(listOf())
    val isLoadingSalesItems = mutableStateOf(false)
    val errorMessage = mutableStateOf("")

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val build: Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        salesItemService = build.create(SalesItemService::class.java)
        getSalesItems()


    }

    fun getSalesItems() {
        isLoadingSalesItems.value = true
        salesItemService.getAllSalesItems().enqueue(object : Callback<List<SalesItem>> {
            override fun onResponse(call: Call<List<SalesItem>>, response: Response<List<SalesItem>>) {
                isLoadingSalesItems.value = false
                if (response.isSuccessful) {
                    val salesItemList: List<SalesItem>? = response.body()
                    salesItems.value = salesItemList ?: emptyList()
                    errorMessage.value = ""
                } else {
                    val message = response.code().toString() + " " + response.message()
                    errorMessage.value = message
                }
            }

            override fun onFailure(call: Call<List<SalesItem>>, t: Throwable) {
                isLoadingSalesItems.value = false
                val message = t.message ?: "No connection to back-end"
                errorMessage.value = message
            }
        })
    }

    fun add(salesItem: SalesItem) {
        salesItemService.createSalesItem(salesItem).enqueue(object : Callback<SalesItem> {
            override fun onResponse(call: Call<SalesItem>, response: Response<SalesItem>) {
                if (response.isSuccessful) {
                    Log.d(tag, "Added: " + response.body())
                    getSalesItems()
                    errorMessage.value = ""
                } else {
                    val message = response.code().toString() + " " + response.message()
                    errorMessage.value = message
                    Log.e(tag, message)
                }
            }

            override fun onFailure(call: Call<SalesItem>, t: Throwable) {
                val message = t.message ?: "No connection to back-end"
                errorMessage.value = message
                Log.e(tag, message)
            }
        })
    }

    fun delete(id: Int) {
        Log.d(tag, "Delete: $id")
        salesItemService.deleteSalesItem(id).enqueue(object : Callback<SalesItem> {
            override fun onResponse(call: Call<SalesItem>, response: Response<SalesItem>) {
                if (response.isSuccessful) {
                    Log.d(tag, "Delete: " + response.body())
                    errorMessage.value = ""
                    getSalesItems()
                } else {
                    val message = response.code().toString() + " " + response.message()
                    errorMessage.value = message
                    Log.e(tag, "Not deleted: $message")
                }
            }

            override fun onFailure(call: Call<SalesItem>, t: Throwable) {
                val message = t.message ?: "No connection to back-end"
                errorMessage.value = message
                Log.e(tag, "Not deleted $message")
            }
        })
    }

    fun sortSalesItemsByPrice(ascending: Boolean) {
        Log.d(tag, "Sort by price")
        salesItems.value = if (ascending) {
            salesItems.value.sortedBy { it.price }
        } else {
            salesItems.value.sortedByDescending { it.price }
        }
    }

    fun sortSalesItemsByDescription(ascending: Boolean) {
        Log.d(tag, "Sort by description")
        salesItems.value = if (ascending) {
            salesItems.value.sortedBy { it.description }
        } else {
            salesItems.value.sortedByDescending { it.description }
        }
    }

    fun filterSalesItemsByMaxPrice(maxPrice: String) {
        if (maxPrice.isEmpty()) {
            getSalesItems()
            return
        }
        val max = maxPrice.toDoubleOrNull()
        if (max != null) {
            salesItems.value = salesItems.value.filter { it.price <= max }
        }
    }

    fun filterSalesItemsByDescription(keyword: String) {
        if (keyword.isEmpty()) {
            getSalesItems()
            return
        }
        salesItems.value = salesItems.value.filter { it.description.contains(keyword, ignoreCase = true) }
    }
}