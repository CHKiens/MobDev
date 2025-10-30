package com.example.mobdev

sealed class NavRoutes (val route: String){
    data object SalesItemList : NavRoutes("list")
    data object SalesItemAdd : NavRoutes("add")
    data object SalesItemDetails : NavRoutes("details")
    data object Login : NavRoutes("login")
    data object Register : NavRoutes("register")
}