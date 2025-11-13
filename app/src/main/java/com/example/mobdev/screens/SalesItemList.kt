package com.example.mobdev.screens

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.InspectableModifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.mobdev.model.SalesItem
import com.example.mobdev.ui.theme.Blueish
import com.example.mobdev.ui.theme.Yellowish

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
)

{
    val orientation = LocalConfiguration.current.orientation
    val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE
    Scaffold(
        topBar = {
            if (!isLandscape) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    title = { Text("Sales Items") },
                    actions = {
                        IconButton(
                            onClick = onAccountClick,
                            modifier = Modifier
                                .padding(24.dp)
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.secondary, CircleShape)
                        ) {
                            Icon(
                                Icons.Filled.PersonOutline,
                                contentDescription = "Account",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                    }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            if (isLandscape) {
                FloatingActionButton(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    onClick = onAccountClick,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.PersonOutline,
                        contentDescription = "Account",
                        modifier = Modifier.fillMaxSize())
                }
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

        var descFragment by rememberSaveable { mutableStateOf("") }
        var maxPrice by rememberSaveable { mutableStateOf("") }
        var sortDescAscending by rememberSaveable { mutableStateOf(true) }
        var sortPriceAscending by rememberSaveable { mutableStateOf(true) }

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
            OutlinedButton(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                onClick = {
                sortByDescription(sortDescAscending)
                sortDescAscending = !sortDescAscending
            }) {
                Text(if (sortDescAscending) "Description ↑" else "Description ↓")
            }

            OutlinedButton(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                onClick = {
                sortByPrice(sortPriceAscending)
                sortPriceAscending = !sortPriceAscending
            }) {
                Text(if (sortPriceAscending) "Price ↑" else "Price ↓")
            }
            Spacer(Modifier.weight(1f))
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
            horizontalArrangement = Arrangement.Start
        ) {
            val brush = Brush.verticalGradient(
                colors = listOf(Blueish, Yellowish)
            )
            Box(
                Modifier
                    .size(120.dp),
            ) {
                val cornerRadius = 8.dp
                val boxSize = 100.dp

                if (isValidUrl(salesItem.pictureUrl)) {
                    Image(
                        painter = rememberAsyncImagePainter(salesItem.pictureUrl),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(boxSize)
                            .clip(RoundedCornerShape(cornerRadius))
                    )
                } else {
                    Canvas(
                        modifier = Modifier
                            .padding(12.dp)
                            .size(boxSize)
                            .clip(RoundedCornerShape(cornerRadius))
                    ) {
                        drawRoundRect(
                            brush = brush,
                            cornerRadius = CornerRadius(16f, 16f)
                        )
                    }
                }
            }

            Column(Modifier.padding(top = 16.dp)) {
                Text(
                    modifier = Modifier.width(150.dp).padding(bottom = 2.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    text = salesItem.description,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
                Text(
                    text = "${salesItem.price} DKK",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    modifier = Modifier.padding(top = 20.dp),
                    text = getDateTimeFromUnix(salesItem.time),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (salesItem.sellerEmail == currentUserEmail) {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Remove ${salesItem.description}",
                    modifier = Modifier
                        .padding(36.dp)
                        .clickable { onSalesItemDeleted(salesItem) }
                )
            }
        }
    }
}

