package com.example.gtk_maps

class ClassRoute: java.io.Serializable {
    private var startPlace: ClassPlace? = null
    private var places: ArrayList<ClassPlace> = ArrayList()
    private var transportMode: String? = null

    fun setStartPlace(startPlace: ClassPlace){
        this.startPlace = startPlace
    }
    fun getStartPlace(): ClassPlace?{
        return this.startPlace
    }
    fun setPlaces(places: ArrayList<ClassPlace>){
        this.places = places
    }
    fun getPlaces(): ArrayList<ClassPlace> {
        return this.places
    }
    fun addPlace(place: ClassPlace){
        this.places.add(place)
    }
    fun removePlace(place: ClassPlace){
        this.places.remove(place)
    }
    fun setTransportMode(transportMode: String){
        this.transportMode = transportMode
    }
    fun getTransportMode(): String?{
        return this.transportMode
    }
}