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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mobdev.model.SalesItem
import com.example.mobdev.model.SalesItemViewModel
import com.example.mobdev.screens.Login
import com.example.mobdev.screens.Register
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
    viewModel: SalesItemViewModel = viewModel()
) {
    val navController = rememberNavController()
    val salesItems = viewModel.salesItems.value
    val errorMessage = viewModel.errorMessage.value

    NavHost(navController = navController, startDestination = NavRoutes.SalesItemList.route) {
        composable(NavRoutes.SalesItemList.route) {
            SalesItemList(
                modifier = modifier,
                salesItems = salesItems,
                onSalesItemsReload = { viewModel.getSalesItems() },
                onSalesItemSelected =
                    { salesItem ->
                        navController.navigate(NavRoutes.SalesItemDetails.route + "/${salesItem.id}") },
                onSalesItemDeleted = { salesItem -> viewModel.delete(salesItem) },
                sortByPrice = { up -> viewModel.sortByPrice(up) },
                sortByDescription = { up -> viewModel.sortByDescription(up) },
                filterByMaxPrice = { maxPrice -> viewModel.filterByMaxPrice(maxPrice.toString()) },
                filterByDescription = { keyword -> viewModel.filterByDescription(keyword) },
                isLoading = viewModel.isLoadingSalesItems.value,
                errorMessage = errorMessage
            )
        }
        composable(
            route = NavRoutes.SalesItemDetails.route + "/{salesItemId}",
            arguments = listOf(navArgument("salesItemId") { type = NavType.IntType})
        ) { backStackEntry ->
            val salesItemId = backStackEntry.arguments?.getInt("salesItemId")
            val salesItem = salesItems.find { it.id == salesItemId } ?: SalesItem(
                description = "Not found",
                price = 0.0,
                time = 0
            )
            SalesItemDetails(
                modifier = modifier,
                salesItem = salesItem,
                navigateBack = { navController.popBackStack() }
            ) }
        composable(NavRoutes.SalesItemAdd.route) {
            SalesItemAdd(
                modifier = modifier,
                addSalesItem = { salesItem -> viewModel.add(salesItem) },
                navigateBack = { navController.popBackStack() })
        }
        composable (NavRoutes.UserScreen.route){
            UserScreen(
                modifier = modifier,
                navigateBack = { navController.popBackStack() }

            )
        }
        composable (NavRoutes.Register.route){
            Register(
                modifier = modifier,
                navigateBack = { navController.popBackStack() },
                onNavigateToLogin = { navController.navigate(NavRoutes.Login.route) }
            )
        }
        composable (NavRoutes.Login.route){
            Login(
                modifier = modifier,
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