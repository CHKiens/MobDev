package com.example.mobdev.screens

import android.accessibilityservice.GestureDescription
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import com.example.mobdev.model.SalesItem
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesItemList (
    modifier: Modifier = Modifier,
    salesItems: List<SalesItem>,
    isLoading: Boolean,
    onSalesItemSelected: (SalesItem) -> Unit = {},
    onSalesItemDeleted: (SalesItem) -> Unit = {},
    onSalesItemsReload: () -> Unit = {},
    sortByPrice: (up: Boolean) -> Unit = {},
    sortByDescription: (up: Boolean) -> Unit = {},
    filterByMaxPrice: (maxPrice: Double?) -> Unit = {},
    filterByDescription: (String) -> Unit = {},
    errorMessage: String,
    onAdd: () -> Unit = {}
){
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                title = { Text("Sales Items") }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                onClick = { onAdd() },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Filled.PersonOutline, contentDescription = "Account",
                    modifier = Modifier.fillMaxSize())
            }
        })
    { innerPadding ->
        SalesItemListContent(
            salesItems = salesItems,
            isLoading = isLoading,
            onSalesItemSelected = onSalesItemSelected,
            onSalesItemDeleted = onSalesItemDeleted,
            onSalesItemsReload = onSalesItemsReload,
            onFilterSalesItemsByMaxPrice = filterByMaxPrice,
            onFilterSalesItemsByDescription = filterByDescription,
            sortByPrice = sortByPrice,
            sortByDescription = sortByDescription,
            errorMessage = errorMessage,
            modifier = Modifier.padding(innerPadding)
        )

    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SalesItemListContent(
    modifier: Modifier = Modifier,
    salesItems: List<SalesItem>,
    isLoading: Boolean,
    onSalesItemSelected: (SalesItem) -> Unit,
    onSalesItemDeleted: (SalesItem) -> Unit,
    onSalesItemsReload: () -> Unit = {},
    onFilterSalesItemsByMaxPrice: (Double) -> Unit,
    onFilterSalesItemsByDescription: (String) -> Unit,
    sortByPrice: (up: Boolean) -> Unit,
    sortByDescription: (up: Boolean) -> Unit,
    errorMessage: String
) {
    Column (modifier = modifier.padding(8.dp)){
        if (errorMessage.isNotEmpty()){
            Text(text = "Problem: $errorMessage", color = Color.Red)
        }
        val descUp = "Description ↑"
        val descDown = "Description ↓"
        val priceUp = "Price ↑"
        val priceDown = "Price ↓"
        var sortDescAscending by remember { mutableStateOf(true) }
        var sortPriceAscending by remember { mutableStateOf(true) }
        var descFragment by remember { mutableStateOf("") }
        var maxPrice by remember { mutableStateOf("") }

        Row(verticalAlignment = Alignment.CenterVertically)
        {
            TextField(
                value = descFragment,
                onValueChange = {
                    descFragment = it
                },
                label = { Text("Filter by description", style = MaterialTheme.typography.bodySmall) },
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically)
        {
            TextField(
                value = maxPrice,
                onValueChange = {
                    maxPrice = it
                },
                label = { Text("Filter by max price", style = MaterialTheme.typography.bodySmall) },
                modifier = Modifier.padding(end = 8.dp)
            )
            Button(onClick = {
                val max = maxPrice.toDoubleOrNull()
                onFilterSalesItemsByDescription(descFragment)
                if (maxPrice.isEmpty() || max == null) {
                    onFilterSalesItemsByMaxPrice(Double.MAX_VALUE)
                } else {
                    onFilterSalesItemsByMaxPrice(max)
                }
            }) {
                Text("Filter")
            }
        }

        Row{
            OutlinedButton(onClick = {
                sortByDescription(sortDescAscending)
                sortDescAscending = !sortDescAscending
            }) {
                Text(if (sortDescAscending) descUp else descDown)
            }
            TextButton(onClick = {
                sortByPrice(sortPriceAscending)
                sortPriceAscending = !sortPriceAscending
            }) {
                Text(if (sortPriceAscending) priceUp else priceDown)
            }
        }
        val orientation = LocalConfiguration.current.orientation
        val columns = if (orientation == Configuration.ORIENTATION_PORTRAIT) 1 else 2

        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { onSalesItemsReload() }
        ) {
            LazyVerticalGrid(columns = GridCells.Fixed(columns)) {
                items(salesItems) { salesItem ->
                    SalesItemCard(
                        salesItem,
                        onSalesItemSelected = onSalesItemSelected,
                        onSalesItemDeleted = onSalesItemDeleted
                    )
                }
            }
        }
    }
}

@Composable
fun SalesItemCard(
    salesItem: SalesItem,
    modifier: Modifier = Modifier,
    onSalesItemSelected: (SalesItem) -> Unit = {},
    onSalesItemDeleted: (SalesItem) -> Unit = {}
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxSize(), onClick = { onSalesItemSelected(salesItem) }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = salesItem.description + ": " + salesItem.price.toString()
            )
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Remove " + salesItem.description,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onSalesItemDeleted(salesItem) }
            )
        }
    }
}

@Preview
@Composable
fun SalesItemsListPreview() {
    SalesItemList(
        salesItems = listOf(
            SalesItem(id = 1, description = "Item 1", price = 10.0, time = 1625155200),
            SalesItem(id = 2, description = "Item 2", price = 20.0, time = 1625241600),
            SalesItem(id = 3, description = "Item 3", price = 15.0, time = 1625328000)
        ),
        errorMessage = "Some error message",
        isLoading = false
    )
}