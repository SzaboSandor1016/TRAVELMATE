package com.example.gtk_maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ViewModelPhoton (private val repository: DataRepository): ViewModel() {

    private val _autoCompleteResults = MutableLiveData<ArrayList<ClassPlace>>()
    val autoCompleteResults: LiveData<ArrayList<ClassPlace>> get() = _autoCompleteResults

    private val _reverseGeoCodeResults = MutableLiveData<ArrayList<ClassPlace>>()
    val reverseGeoCodeResults: LiveData<ArrayList<ClassPlace>> get() = _reverseGeoCodeResults

    private val _autoCompleteErrorMessage = MutableLiveData<String?>()
    val autoCompleteErrorMessage: LiveData<String?> get() = _autoCompleteErrorMessage

    private val _reverseGeoCodeErrorMessage = MutableLiveData<String?>()
    val reverseGeoCodeErrorMessage: LiveData<String?> get() = _reverseGeoCodeErrorMessage

    fun searchAutoComplete(query: String) {
        viewModelScope.launch {
            try {

                val places = repository.searchAutocomplete(query)
                _autoCompleteResults.postValue(places)
            } catch (e: Exception) {

                Log.e("PhotonViewModel", "Error fetching places in ViewModelPhoton from Photon API", e)
                _autoCompleteErrorMessage.postValue("Hiba történt a keresés közben. Kérlek próbáld újra.")
            }
        }
    }
    fun searchReverseGeoCode(coordinates: ClassCoordinates) {
        viewModelScope.launch {
            try {

                val places = repository.searchReverseGeoCode(coordinates)
                _reverseGeoCodeResults.postValue(places)
            } catch (e:Exception){

                Log.e("PhotonViewModel", "Error fetching places in ViewModelPhoton from Photon API", e)
                _reverseGeoCodeErrorMessage.postValue("Hiba történt a keresés közben. Kérlek próbáld újra.")
            }
        }
    }



}