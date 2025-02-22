package com.example.gtk_maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModelTrip: ViewModel() {

    private val _tripPlaces = MutableLiveData<ArrayList<ClassPlace>>()
    val tripPlaces: LiveData<ArrayList<ClassPlace>> = _tripPlaces

    private var trip = ClassTrip()

    fun getCurrentTrip(): ClassTrip = trip

    fun setCurrentTrip(trip: ClassTrip) {
        this.trip = trip
        _tripPlaces.value = trip.getPlaces()
    }

    fun setUUID(){
        trip.setUUID()
    }

    fun getUUID(): String? {
        return trip.getUUID()
    }

    fun setTripStartPlace(startPlace: ClassPlace){
        trip.setStartPlace(startPlace)
    }
    fun getTripStartPlace(): ClassPlace?{
        return trip.getStartPlace()
    }
    fun setTripPlaces(places: ArrayList<ClassPlace>){

        trip.setPlaces(places).also {

            _tripPlaces.value = places
        }
    }
    fun getTripPlaces(): ArrayList<ClassPlace>{
        return trip.getPlaces()
    }
    fun addPlaceToTrip(place: ClassPlace){

        trip.addPlace(place)
        _tripPlaces.value = trip.getPlaces()
    }
    fun removePlaceFromTrip(place: ClassPlace){

        trip.removePlace(place)
        _tripPlaces.value = trip.getPlaces()
    }
    fun setTripDate(date: String){
        trip.setDate(date)
    }
    fun getTripDate(): String?{
        return trip.getDate()
    }
    fun setTripTitle(title: String){
        trip.setTitle(title)
    }
    fun getTripTitle(): String?{
        return trip.getTitle()
    }
    fun setTripNote(note: String){
        trip.setNote(note)
    }
    fun getTripNote(): String?{
        return trip.getNote()
    }
    fun addTripContributors(contributors: ArrayList<String>){
        trip.addContributors(contributors)
    }
    fun addTripContributor(contributor: String){
        trip.addContributor(contributor)
    }
    fun removeTripContributors(contributors: ArrayList<String>){
        trip.removeContributors(contributors)
    }
    fun removeTripContributor(contributor: String){
        trip.removeContributor(contributor)
    }

    fun isPlaceContainedByTrip(place: ClassPlace): Boolean{
        return trip.isPlaceContained(place)
    }
    fun isTripPlacesEmpty(): Boolean{
        return trip.isPlacesEmpty()
    }
    fun getTripPlaceCount(): Int{
        return trip.getSize()
    }

    fun clearTrip(){
        trip.clear().also {
            _tripPlaces.value = ArrayList()
        }
    }
}