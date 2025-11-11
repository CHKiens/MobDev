package com.example.mobdev.screens

import android.graphics.drawable.GradientDrawable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobdev.model.SalesItem
import com.example.mobdev.ui.theme.Blueish
import com.example.mobdev.ui.theme.Yellowish
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.res.Configuration
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.platform.LocalConfiguration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesItemDetails (
    salesItem: SalesItem,
    modifier: Modifier = Modifier,
    navigateBack : () -> Unit = {}
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
                title = { Text("Sales Item Details") },
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }

            )

        }
    ){
        Column(modifier = Modifier.padding(it).padding(24.dp)) {
            Text(description, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            Text("Created at: ${getDateTimeFromUnix(salesItem.time)}", style = MaterialTheme.typography.bodySmall)
            val brush = Brush.verticalGradient(
                colors = listOf(Blueish, Yellowish)
            )
            Box(
                Modifier
                    .size(350.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .padding(12.dp)
                        .size(350.dp),
                    onDraw = {
                        drawRoundRect(brush, cornerRadius = CornerRadius(16f, 16f))
                    }
                )
            }
            Text("Price: $price", style = MaterialTheme.typography.bodyLarge)
            Text("Seller Email: ${salesItem.sellerEmail}", style = MaterialTheme.typography.bodySmall)
            Text("Seller Phone: ${salesItem.sellerPhone}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

fun getDateTimeFromUnix(seconds: Int): String {
    val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    val netDate = Date(seconds * 1000L)
    return sdf.format(netDate)
}
