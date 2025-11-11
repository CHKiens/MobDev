package com.example.mobdev.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobdev.model.SalesItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesItemAdd (
    modifier: Modifier = Modifier,
    addSalesItem: (SalesItem) -> Unit = {},
    navigateBack: () -> Unit = {}
    ){
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var descError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }
    Scaffold (
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Add Sales Item") },
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
                )
        }) {innerPadding->
        Column(modifier = modifier.padding(innerPadding).padding(8.dp)){
            val orientation = LocalConfiguration.current.orientation
            val isPortrait = orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT
            if(isPortrait){
                OutlinedTextField(onValueChange = {description = it},
                    value = description,
                    isError = descError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Description") })
                OutlinedTextField(onValueChange = { price = it },
                    value = price,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = priceError,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Price") })
            } else {
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedTextField(onValueChange = { description = it },
                        value = description,
                        isError = descError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.weight(1f),
                        label = { Text(text = "Description") })
                    OutlinedTextField(onValueChange = { price = it },
                        value = price,
                        // https://medium.com/@GkhKaya00/exploring-keyboard-types-in-kotlin-jetpack-compose-ca1f617e1109
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = priceError,
                        modifier = Modifier.weight(1f),
                        label = { Text(text = "Price") })
                }
            }
            Row(
                modifier = modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { navigateBack() }) {
                    Text("Back")
                }
                Button(onClick = {
                    if (description.isEmpty()) {
                        descError = true
                        return@Button
                    }
                    if (price.isEmpty()) {
                        priceError = true
                        return@Button
                    }
                    val price = price.toDoubleOrNull()
                    if (price == null) {
                        priceError = true
                        return@Button
                    }
                    val salesItem = SalesItem(description = description, price = price)
                    addSalesItem(salesItem)
                    navigateBack()
                }) {
                    Text("Add")
                }
            }
        }
    }
}

@Preview
@Composable
fun SalesItemAddPreview() {
    SalesItemAdd()
}