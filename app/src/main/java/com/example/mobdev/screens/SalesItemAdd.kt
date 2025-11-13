package com.example.mobdev.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mobdev.auth.AuthViewModel
import com.example.mobdev.model.SalesItem
import android.widget.Toast
import androidx.compose.runtime.saveable.rememberSaveable
import java.net.URL

fun isValidUrl(url: String): Boolean {
    if (url.isBlank()) return true
    return try {
        URL(url).toURI()
        true
    } catch (e: Exception) {
        false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesItemAdd(
    modifier: Modifier = Modifier,
    currentUserEmail: String?,
    addSalesItem: (SalesItem) -> Unit = {},
    navigateBack: () -> Unit = {}
) {
    var description by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var img by rememberSaveable { mutableStateOf("") }
    var descError by rememberSaveable { mutableStateOf(false) }
    var priceError by rememberSaveable { mutableStateOf(false) }
    var imgError by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Sales Item") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (currentUserEmail == null) {
                Text(
                    "You must be logged in to add a sales item.",
                    color = MaterialTheme.colorScheme.error
                )
                return@Column
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                isError = descError,
                label = { Text("Description", color = MaterialTheme.colorScheme.secondary) },
                modifier = Modifier.fillMaxWidth()
            )
            if (descError) {
                Text("Description cannot be empty", color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                isError = priceError,
                label = { Text("Price", color = MaterialTheme.colorScheme.secondary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),

                )
            if (priceError) {
                Text("Enter a valid number", color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = img,
                onValueChange = { img = it },
                isError = imgError,
                label = { Text("Image URL", color = MaterialTheme.colorScheme.secondary) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (imgError) {
                Text("Enter a valid URL", color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    descError = description.isBlank()
                    priceError = price.toIntOrNull() == null
                    imgError = !isValidUrl(img)
                    if (descError || priceError || imgError) return@Button

                    val currentTimeInMillis = System.currentTimeMillis()
                    val currentTimeInSeconds = (currentTimeInMillis / 1000)
                    val newItem = SalesItem(
                        id = 0,
                        description = description,
                        price = price.toInt(),
                        sellerEmail = currentUserEmail,
                        sellerPhone = "88888888",
                        pictureUrl = if(isValidUrl(img)) img else "",
                        time = currentTimeInSeconds
                    )
                    try {
                        addSalesItem(newItem)
                        Toast.makeText(context, "Item added successfully!", Toast.LENGTH_SHORT).show()
                        navigateBack()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to add item: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text("Add Sales Item", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
