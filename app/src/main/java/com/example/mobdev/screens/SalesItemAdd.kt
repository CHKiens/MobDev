package com.example.mobdev.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mobdev.auth.AuthViewModel
import com.example.mobdev.model.SalesItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesItemAdd(
    currentUserEmail: String?,
    addSalesItem: (SalesItem) -> Unit = {},
    navigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var descError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }

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
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            if (descError) {
                Text("Description cannot be empty", color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                isError = priceError,
                label = { Text("Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            if (priceError) {
                Text("Enter a valid number", color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    descError = description.isBlank()
                    priceError = price.toIntOrNull() == null
                    if (descError || priceError) return@Button

                    val newItem = SalesItem(
                        id = 0,
                        description = description,
                        price = price.toInt(),
                        sellerEmail = currentUserEmail,
                        sellerPhone = "",
                        pictureUrl = "",
                        time = 0
                    )
                    addSalesItem(newItem)
                    navigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text("Add Sales Item", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}
