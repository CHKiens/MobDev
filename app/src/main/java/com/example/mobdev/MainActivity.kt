package com.example.mobdev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mobdev.auth.AuthViewModel
import com.example.mobdev.model.SalesItem
import com.example.mobdev.model.SalesItemViewModel
import com.example.mobdev.screens.SalesItemAdd
import com.example.mobdev.screens.SalesItemDetails
import com.example.mobdev.screens.SalesItemList
import com.example.mobdev.screens.UserScreen
import com.example.mobdev.ui.theme.MobDevTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        setContent {
            MobDevTheme {
                MainScreen()

            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    salesViewModel: SalesItemViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()
    val salesItems = salesViewModel.salesItems.value
    val errorMessage = salesViewModel.errorMessage.value
    val currentUserEmail = authViewModel.user.value?.email

    NavHost(navController = navController, startDestination = NavRoutes.SalesItemList.route) {

        composable(NavRoutes.SalesItemList.route) {
            SalesItemList(
                salesItems = salesItems,
                currentUserEmail = currentUserEmail,
                isLoading = salesViewModel.isLoadingSalesItems.value,
                onSalesItemSelected = { item ->
                    navController.navigate("${NavRoutes.SalesItemDetails.route}/${item.id}")
                },
                onSalesItemDeleted = { salesViewModel.delete(it) },
                onSalesItemsReload = { salesViewModel.getSalesItems() },
                sortByPrice = { up -> salesViewModel.sortByPrice(up) },
                sortByDescription = { up -> salesViewModel.sortByDescription(up) },
                filterByMaxPrice = { max -> salesViewModel.filterByMaxPrice(max.toString()) },
                filterByDescription = { keyword -> salesViewModel.filterByDescription(keyword) },
                errorMessage = errorMessage,
                onAccountClick = { navController.navigate(NavRoutes.UserScreen.route) }
            )
        }

        composable(
            route = "${NavRoutes.SalesItemDetails.route}/{salesItemId}",
            arguments = listOf(navArgument("salesItemId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("salesItemId")
            val item = salesItems.find { it.id == id } ?: SalesItem(description = "Not found", price =  0)
            SalesItemDetails(
                salesItem = item,
                navigateBack = { navController.popBackStack() },
                currentUserEmail = currentUserEmail,
                onSalesItemDeleted = { salesViewModel.delete(it) }
            )
        }

        composable(NavRoutes.UserScreen.route) {
            UserScreen(
                navController = navController,
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.SalesItemAdd.route) {
            SalesItemAdd(
                currentUserEmail = currentUserEmail,
                addSalesItem = { salesViewModel.add(it) },
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MobDevTheme {
        MainScreen()
    }
}