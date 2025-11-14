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
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import coil.compose.rememberAsyncImagePainter
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesItemDetails (
    salesItem: SalesItem,
    modifier: Modifier = Modifier,
    onSalesItemDeleted : (SalesItem) -> Unit = {},
    currentUserEmail : String? = null,
    navigateBack : () -> Unit = {}
)
{
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
    )
    {

        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val brush = Brush.verticalGradient(
            colors = listOf(Blueish, Yellowish)
        )

        if (isLandscape) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.padding(it).padding(24.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val cornerRadius = 16.dp
                    val boxSize = 350.dp

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

                Column(
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxHeight()
                        .padding(start = 8.dp)
                ) {
                    Text(description, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    Text("Created at: ${getDateTimeFromUnix(salesItem.time)}", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Price: $price", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 16.dp))
                    Text("Seller Email:", style = MaterialTheme.typography.bodySmall)
                    Text(salesItem.sellerEmail, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Seller Phone:", style = MaterialTheme.typography.bodySmall)
                    Text(salesItem.sellerPhone, style = MaterialTheme.typography.bodySmall)
                    if (salesItem.sellerEmail == currentUserEmail) {
                        Button(
                            onClick = {
                                onSalesItemDeleted(salesItem)
                                navigateBack()
                            },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Remove ${salesItem.description}"
                            )
                            Text(
                                "Delete Item",
                                modifier = Modifier.padding(start = 8.dp),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
            }
        } else {
            Column(modifier = Modifier.padding(it).padding(24.dp)) {
                Text(description, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                Text("Created at: ${getDateTimeFromUnix(salesItem.time)}", style = MaterialTheme.typography.bodySmall)
                Box(
                    Modifier
                        .size(350.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val cornerRadius = 16.dp
                    val boxSize = 350.dp

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
                Text("Price: $price", style = MaterialTheme.typography.bodyLarge)
                Text("Seller Email: ${salesItem.sellerEmail}", style = MaterialTheme.typography.bodySmall)
                Text("Seller Phone: ${salesItem.sellerPhone}", style = MaterialTheme.typography.bodySmall)
                if (salesItem.sellerEmail == currentUserEmail) {
                    Button(
                        onClick = {
                            onSalesItemDeleted(salesItem)
                            navigateBack()
                        },
                        modifier = Modifier.padding(top = 16.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Remove ${salesItem.description}"
                        )
                        Text(
                            "Delete Item",
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }
        }
    }
}


fun getDateTimeFromUnix(seconds: Long): String {
    val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    val netDate = Date(seconds * 1000L)
    return sdf.format(netDate)
}


