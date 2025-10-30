package com.example.mobdev.model

import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.example.mobdev.repository.SalesItemRepository
import kotlinx.coroutines.flow.StateFlow

class SalesItemViewModel : ViewModel(){
    private val repository = SalesItemRepository()
    val salesItems: State<List<SalesItem>> = repository.salesItems
    val errorMessage: State<String> = repository.errorMessage
    val isLoadingSalesItems: State<Boolean> = repository.isLoadingSalesItems

    fun getSalesItems() {
        repository.getSalesItems()
    }

    fun add(salesItem: SalesItem) {
        repository.add(salesItem)
    }

    fun delete(salesItem: SalesItem) {
        repository.delete(salesItem.id)
    }

    fun sortByPrice(up: Boolean) {
        repository.sortSalesItemsByPrice(up)
    }

    fun sortByDescription(up: Boolean) {
        repository.sortSalesItemsByDescription(up)
    }

    fun filterByMaxPrice(maxPrice: String) {
        repository.filterSalesItemsByMaxPrice(maxPrice)
    }

    fun filterByDescription(keyword: String) {
        repository.filterSalesItemsByDescription(keyword)
    }


}