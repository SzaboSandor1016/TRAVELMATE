package com.example.gtk_maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModelFragmentPlaceDetails: ViewModel() {

    private val _containerState: MutableLiveData<String> = MutableLiveData<String>()
    val containerState: LiveData<String> = _containerState


    fun setContainerState(state: String){
        this._containerState.postValue(state)
    }

}