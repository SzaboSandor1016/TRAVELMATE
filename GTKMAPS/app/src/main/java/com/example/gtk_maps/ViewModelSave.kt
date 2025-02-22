package com.example.gtk_maps

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ViewModelSave (private val repository: DataRepository): ViewModel() {


    private val SAVED_TRIPS_FILE_NAME = "saved_trips.json"
    private val TRIP_CLASS_TYPE = ClassTrip::class.java


    private val _trips = MutableLiveData<ArrayList<ClassTrip>>()
    val trips: LiveData<ArrayList<ClassTrip>> = _trips

    private val _writeErrorMessage = MutableLiveData<String?>()
    val writeErrorMessage: LiveData<String?> = _writeErrorMessage

    private val _readErrorMessage = MutableLiveData<String?>()
    val readErrorMessage: LiveData<String?> = _readErrorMessage

    private var allTrips: ArrayList<ClassTrip> = ArrayList()

    fun setTrips(trips: ArrayList<ClassTrip>){
        _trips.value = trips
        allTrips.addAll(trips)
    }
    fun removeTrip(trip: ClassTrip){
        allTrips.remove(trip)
        _trips.value = allTrips
    }

    fun writeTripsToFile(context: Context, tripsToWrite: ArrayList<ClassTrip>){

        viewModelScope.launch {
            try {
                val actualContent = repository.readStorage(SAVED_TRIPS_FILE_NAME, TRIP_CLASS_TYPE)
                actualContent.addAll(tripsToWrite)
                repository.writeStorage(actualContent , SAVED_TRIPS_FILE_NAME)
            }catch (e: Exception){
                Log.e("FileWriter", "Error writing to file: saved_trips.json \n error:", e)
                _writeErrorMessage.postValue("Error writing to file")
            }
        }
    }
    fun writeTripToFile(context: Context, tripToWrite: ClassTrip){

        viewModelScope.launch {
            try {
                val actualContent: ArrayList<ClassTrip> = ArrayList()

                if (repository.checkFileExists(SAVED_TRIPS_FILE_NAME)) {
                     val content = repository.readStorage( SAVED_TRIPS_FILE_NAME, TRIP_CLASS_TYPE)
                    actualContent.addAll(content)
                }
                actualContent.add(tripToWrite)
                repository.writeStorage( actualContent , SAVED_TRIPS_FILE_NAME)
            }catch (e: Exception){
                Log.e("FileWriter", "Error writing to file: saved_trips.json \n error:", e)
                _writeErrorMessage.postValue("Error writing to file")
            }
        }
    }

    fun readSavedTrips(){
        viewModelScope.launch {
            try {
                val trips = repository.readStorage( SAVED_TRIPS_FILE_NAME, TRIP_CLASS_TYPE)
                _trips.postValue(trips)
            }catch (e: Exception){
                Log.e("FileReader", "Error reading file: saved_trips.json \n error:", e)
                _readErrorMessage.postValue("Error reading file")
            }
        }
    }

}