package com.example.mobdev.screens

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mobdev.model.SalesItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesItemList(
    modifier: Modifier = Modifier,
    salesItems: List<SalesItem>,
    currentUserEmail: String? = null,
    isLoading: Boolean = false,
    onSalesItemSelected: (SalesItem) -> Unit = {},
    onSalesItemDeleted: (SalesItem) -> Unit = {},
    onSalesItemsReload: () -> Unit = {},
    sortByPrice: (up: Boolean) -> Unit = {},
    sortByDescription: (up: Boolean) -> Unit = {},
    filterByMaxPrice: (maxPrice: Int?) -> Unit = {},
    filterByDescription: (String) -> Unit = {},
    errorMessage: String = "",
    onAccountClick : () -> Unit = {},
) {
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
                onClick = onAccountClick,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.PersonOutline, contentDescription = "Account", modifier = Modifier.fillMaxSize())
            }
        }
    ) { innerPadding ->
        SalesItemListContent(
            salesItems = salesItems,
            currentUserEmail = currentUserEmail,
            isLoading = isLoading,
            onSalesItemSelected = onSalesItemSelected,
            onSalesItemDeleted = onSalesItemDeleted,
            onSalesItemsReload = onSalesItemsReload,
            onFilterSalesItemsByMaxPrice = filterByMaxPrice,
            onFilterSalesItemsByDescription = filterByDescription,
            sortByPrice = sortByPrice,
            sortByDescription = sortByDescription,
            errorMessage = errorMessage,
            modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun SalesItemListContent(
    modifier: Modifier = Modifier,
    salesItems: List<SalesItem>,
    currentUserEmail: String? = null,
    isLoading: Boolean,
    onSalesItemSelected: (SalesItem) -> Unit,
    onSalesItemDeleted: (SalesItem) -> Unit,
    onSalesItemsReload: () -> Unit = {},
    onFilterSalesItemsByMaxPrice: (Int) -> Unit,
    onFilterSalesItemsByDescription: (String) -> Unit,
    sortByPrice: (up: Boolean) -> Unit,
    sortByDescription: (up: Boolean) -> Unit,
    errorMessage: String
) {
    Column(modifier = modifier.padding(8.dp)) {
        if (errorMessage.isNotEmpty()) {
            Text(text = "Problem: $errorMessage", color = Color.Red)
        }

        var descFragment by remember { mutableStateOf("") }
        var maxPrice by remember { mutableStateOf("") }
        var sortDescAscending by remember { mutableStateOf(true) }
        var sortPriceAscending by remember { mutableStateOf(true) }

        Column {
            TextField(
                value = descFragment,
                onValueChange = { descFragment = it },
                label = { Text("Filter by description", style = MaterialTheme.typography.bodySmall) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            TextField(
                value = maxPrice,
                onValueChange = { maxPrice = it },
                label = { Text("Filter by max price", style = MaterialTheme.typography.bodySmall) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }

        Row {
            OutlinedButton(onClick = {
                sortByDescription(sortDescAscending)
                sortDescAscending = !sortDescAscending
            }) {
                Text(if (sortDescAscending) "Description ↑" else "Description ↓")
            }

            OutlinedButton(onClick = {
                sortByPrice(sortPriceAscending)
                sortPriceAscending = !sortPriceAscending
            }) {
                Text(if (sortPriceAscending) "Price ↑" else "Price ↓")
            }
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                ),
                onClick = {
                    val max = maxPrice.toIntOrNull() ?: Int.MAX_VALUE
                    onFilterSalesItemsByDescription(descFragment)
                    onFilterSalesItemsByMaxPrice(max)
                }) {
                Text("Filter", color = MaterialTheme.colorScheme.primary)
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
                        salesItem = salesItem,
                        currentUserEmail = currentUserEmail,
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
    currentUserEmail: String?,
    modifier: Modifier = Modifier,
    onSalesItemSelected: (SalesItem) -> Unit = {},
    onSalesItemDeleted: (SalesItem) -> Unit = {}
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxSize()
            .clickable { onSalesItemSelected(salesItem) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = "${salesItem.description}: ${salesItem.price}"
            )

            if (salesItem.sellerEmail == currentUserEmail) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Remove ${salesItem.description}",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { onSalesItemDeleted(salesItem) }
                )
            }
        }
    }
}

