package com.example.mobdev.repository

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.mobdev.model.SalesItem
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class SalesItemRepository {
    private val baseUrl = "https://anbo-salesitems.azurewebsites.net/api/"
    private val tag = "SalesItemRepository"
    private val salesItemService: SalesItemService

    private var _salesItems = listOf<SalesItem>()

    val salesItems: MutableState<List<SalesItem>> = mutableStateOf(listOf())
    val isLoadingSalesItems = mutableStateOf(false)
    val errorMessage = mutableStateOf("")

    private var keywordFilter = ""
    private var maxPriceFilter: Int? = null
    private var currentSort: Sort = Sort.None

    private sealed class Sort {
        object None : Sort()
        data class ByPrice(val ascending: Boolean) : Sort()
        data class ByDescription(val ascending: Boolean) : Sort()
    }

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder().addInterceptor(logging).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        salesItemService = retrofit.create(SalesItemService::class.java)
        getSalesItems()
    }

    fun getSalesItems() {
        Log.d(tag, "Getting sales items from API")
        isLoadingSalesItems.value = true
        salesItemService.getAllSalesItems().enqueue(object : Callback<List<SalesItem>> {
            override fun onResponse(call: Call<List<SalesItem>>, response: Response<List<SalesItem>>) {
                isLoadingSalesItems.value = false
                if (response.isSuccessful) {
                    response.body()?.let {
                        _salesItems = it
                        applyFiltersAndSorts()
                        errorMessage.value = ""
                        Log.d(tag, "Loaded ${it.size} items successfully")
                    } ?: handleError("Empty response body", response.code())
                } else {
                    handleError("HTTP ${response.code()} ${response.message()}", response.code())
                }
            }

            override fun onFailure(call: Call<List<SalesItem>>, t: Throwable) {
                handleFailure("getSalesItems", t)
            }
        })
    }

    fun add(salesItem: SalesItem) {
        Log.d(tag, "Adding new item: ${salesItem.description}")
        salesItemService.createSalesItem(salesItem).enqueue(object : Callback<SalesItem> {
            override fun onResponse(call: Call<SalesItem>, response: Response<SalesItem>) {
                if (response.isSuccessful) {
                    Log.d(tag, "Added: ${response.body()}")
                    getSalesItems()
                } else {
                    handleError("Failed to add item: ${response.message()}", response.code())
                }
            }

            override fun onFailure(call: Call<SalesItem>, t: Throwable) {
                handleFailure("add", t)
            }
        })
    }

    fun delete(id: Int) {
        Log.d(tag, "Deleting item with id: $id")
        salesItemService.deleteSalesItem(id).enqueue(object : Callback<SalesItem> {
            override fun onResponse(call: Call<SalesItem>, response: Response<SalesItem>) {
                if (response.isSuccessful) {
                    Log.d(tag, "Deleted item: ${response.body()}")
                    getSalesItems()
                } else {
                    handleError("Failed to delete item: ${response.message()}", response.code())
                }
            }

            override fun onFailure(call: Call<SalesItem>, t: Throwable) {
                handleFailure("delete", t)
            }
        })
    }

    fun filterSalesItemsByMaxPrice(maxPrice: String) {
        Log.d(tag, "Setting max price filter: $maxPrice")
        maxPriceFilter = maxPrice.toIntOrNull()
        applyFiltersAndSorts()
    }

    fun filterSalesItemsByDescription(keyword: String) {
        Log.d(tag, "Setting description filter: \"$keyword\"")
        keywordFilter = keyword
        applyFiltersAndSorts()
    }

    fun sortSalesItemsByPrice(ascending: Boolean) {
        Log.d(tag, "Setting sort by price, ascending: $ascending")
        currentSort = Sort.ByPrice(ascending)
        applyFiltersAndSorts()
    }

    fun sortSalesItemsByDescription(ascending: Boolean) {
        Log.d(tag, "Setting sort by description, ascending: $ascending")
        currentSort = Sort.ByDescription(ascending)
        applyFiltersAndSorts()
    }

    private fun applyFiltersAndSorts() {
        var processedList = _salesItems

        maxPriceFilter?.let { max ->
            processedList = processedList.filter { it.price <= max }
        }

        if (keywordFilter.isNotBlank()) {
            processedList = processedList.filter { it.description.contains(keywordFilter, ignoreCase = true) }
        }

        processedList = when (val sort = currentSort) {
            is Sort.ByPrice -> {
                if (sort.ascending) processedList.sortedBy { it.price }
                else processedList.sortedByDescending { it.price }
            }
            is Sort.ByDescription -> {
                if (sort.ascending) processedList.sortedBy { it.description }
                else processedList.sortedByDescending { it.description }
            }
            Sort.None -> processedList
        }
        salesItems.value = processedList
    }

    private fun handleError(message: String, code: Int? = null) {
        val fullMessage = if (code != null) "$message (Code $code)" else message
        errorMessage.value = fullMessage
        Log.e(tag, fullMessage)
    }

    private fun handleFailure(method: String, t: Throwable) {
        val msg = t.message ?: "Unknown error"
        errorMessage.value = msg
        Log.e(tag, "Failure in $method(): $msg", t)
    }
}
