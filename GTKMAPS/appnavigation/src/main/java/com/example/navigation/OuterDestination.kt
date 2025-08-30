package com.example.navigation

sealed class OuterDestination {

    data object Save: OuterDestination()

    data object User: OuterDestination()
}