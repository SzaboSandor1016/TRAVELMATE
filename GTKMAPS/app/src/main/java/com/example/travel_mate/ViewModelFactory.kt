package com.example.travel_mate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class ViewModelFactory(private val tripRepository: TripRepository, private val searchRepository: SearchRepository): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ViewModelUser::class.java) -> {
                ViewModelUser(tripRepository) as T
            }
            modelClass.isAssignableFrom(ViewModelMain::class.java) -> {
                ViewModelMain(searchRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown fragment type")
        }
    }

}