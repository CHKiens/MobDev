package com.example.mobdev.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobdev.model.SalesItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesItemDetails (
    salesItem: SalesItem,
    modifier: Modifier = Modifier,
    onNavigateBack : () -> Unit = {}
){
    val description = salesItem.description
    val price = salesItem.price
    Scaffold (modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                title = { Text("Sales Item Details") })
        }
    ){
        Text(
            text = "Description: $description\nPrice: $price\nCreated at: ${getDateTimeFromUnix(salesItem.time)}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(it).padding(16.dp)
        )
    }
}

fun getDateTimeFromUnix(seconds: Int): String {
    val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    val netDate = Date(seconds * 1000L)
    return sdf.format(netDate)
}

@Preview
@Composable
fun SalesItemDetailsPreview() {
    val sampleItem = SalesItem(
        id = 1,
        description = "Sample Item",
        price = 99.99,
        sellerEmail = "test@mail.com",
        sellerPhone = "88 88 88 88",
        pictureUrl = null,
        userId = "user123",
        time = 1510500494
    )
}