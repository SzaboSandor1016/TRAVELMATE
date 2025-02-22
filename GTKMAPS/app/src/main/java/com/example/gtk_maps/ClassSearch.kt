package com.example.gtk_maps

import android.os.Parcel
import android.os.Parcelable

class ClassSearch() : android.os.Parcelable {

    private val transportModeSpeeds: Map<String, Int> =
        mapOf( "walk" to 3500,
                "car" to 40000)

    private var startPlace: ClassPlace? = null
    private var places: ArrayList<ClassPlace> = ArrayList()
    private var distance: Double?= null
    private var transportMode: String? = null
    private var minute: Int? = null

    constructor(parcel: Parcel) : this() {
        startPlace = parcel.readParcelable(ClassPlace::class.java.classLoader)
        distance = parcel.readValue(Double::class.java.classLoader) as? Double
        transportMode = parcel.readString()
        minute = parcel.readValue(Int::class.java.classLoader) as? Int
    }


    fun setStartPlace(startPlace: ClassPlace){
        this.startPlace = startPlace
    }
    fun getStartPlace(): ClassPlace?{
        return this.startPlace
    }
    fun setPlaces(places: ArrayList<ClassPlace>){
        this.places = places
    }
    fun getPlaces(): ArrayList<ClassPlace>{
        return places
    }
    fun setDistance(distance: Double?){
        this.distance = distance
    }
    fun getDistance(): Double?{
        return this.distance
    }
    fun addPlaces(places: ArrayList<ClassPlace>){
        this.places.addAll(places.toSet())
    }
    fun removePlaces(places: ArrayList<ClassPlace>){
        this.places.removeAll(places.toSet())
    }
    fun addPlace(place: ClassPlace){
        this.places.add(place)
    }
    fun removePlace(place: ClassPlace){
        this.places.remove(place)
    }

    fun setTransportMode(transportMode: String?){
        this.transportMode = transportMode
    }
    fun getTransportMode(): String?{
        return this.transportMode
    }
    fun setMinute(minute: Int?){
        this.minute = minute
    }
    fun getMinute(): Int?{
        return this.minute
    }

    fun calculateDistance(){
        this.distance = (transportModeSpeeds[this.transportMode]?.times(this.minute!!) ?: 0) / 60.0
    }

    fun resetSearchDetails(){
        this.minute = null
        this.distance = null
        this.transportMode = null
        this.places = ArrayList()
    }

    fun removePlacesByCategory(category: String){

        var placesToRemove = places.filter { it.getCategory().equals(category) }

        places.removeAll(placesToRemove.toSet())
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(startPlace, flags)
        parcel.writeValue(distance)
        parcel.writeString(transportMode)
        parcel.writeValue(minute)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ClassSearch> {
        override fun createFromParcel(parcel: Parcel): ClassSearch {
            return ClassSearch(parcel)
        }

        override fun newArray(size: Int): Array<ClassSearch?> {
            return arrayOfNulls(size)
        }
    }

}