package com.example.gtk_maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModelTrip: ViewModel() {

    private val _tripPlaces = MutableLiveData<ArrayList<ClassPlace>>()
    val tripPlaces: LiveData<ArrayList<ClassPlace>> = _tripPlaces

    private var trip = ClassTrip()

    fun getCurrentTrip(): ClassTrip = trip


}