package com.example.gtk_maps

import android.os.Parcel
import android.os.Parcelable

class ClassCoordinates() : android.os.Parcelable {

    private var latitude: Double? = null
    private var longitude: Double? = null

    constructor(parcel: Parcel) : this() {
        latitude = parcel.readValue(Double::class.java.classLoader) as? Double
        longitude = parcel.readValue(Double::class.java.classLoader) as? Double
    }

    constructor(latitude: Double,longitude: Double) : this(){
        this.latitude = latitude
        this.longitude = longitude
    }

    fun setLatitude(latitude: Double){
        this.latitude = latitude
    }
    fun getLatitude(): Double?{
        return this.latitude
    }
    fun setLongitude(longitude: Double){
        this.longitude = longitude
    }
    fun getLongitude(): Double?{
        return this.longitude
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(latitude)
        parcel.writeValue(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ClassCoordinates> {
        override fun createFromParcel(parcel: Parcel): ClassCoordinates {
            return ClassCoordinates(parcel)
        }

        override fun newArray(size: Int): Array<ClassCoordinates?> {
            return arrayOfNulls(size)
        }
    }
}