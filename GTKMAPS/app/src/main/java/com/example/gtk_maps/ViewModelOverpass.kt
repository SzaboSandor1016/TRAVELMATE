package com.example.gtk_maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ViewModelOverpass (private val repository: DataRepository): ViewModel() {

    private val overpassRetrofit = Retrofit.Builder()
        .baseUrl("https://overpass-api.de/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ClassRequests.OverpassApi::class.java)


    private val _overpassResponse = MutableLiveData<ArrayList<ClassPlace>>()
    val overpassResponse : LiveData<ArrayList<ClassPlace>> = _overpassResponse

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun searchOverpass(query: String,category: String){
        viewModelScope.launch {

            try {
                val places = repository.searchOverpass(query,category)
                _overpassResponse.postValue(places)

            }catch (e: Exception){

                Log.e("OverpassViewModel", "Hiba a Overpass API lekérdezésekor", e)
                _errorMessage.postValue("Hiba történt a keresés közben. Kérlek próbáld újra.")
            }
        }
    }

}