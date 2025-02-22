package com.example.gtk_maps

import android.os.Parcel
import android.os.Parcelable

class ClassPlace() : android.os.Parcelable {

    private var name: String? = null
    private var cuisine: String? = null
    private var openingHours: String? = null
    private var charge: String? = null
    private var address: ClassAddress? = null
    private var coordinates: ClassCoordinates? = null
    private var category: String? = null
    private var containedByTrip: Boolean = false

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        cuisine = parcel.readString()
        openingHours = parcel.readString()
        charge = parcel.readString()
        address = parcel.readParcelable(ClassAddress::class.java.classLoader)
        coordinates = parcel.readParcelable(ClassCoordinates::class.java.classLoader)
        category = parcel.readString()
        containedByTrip = parcel.readByte() != 0.toByte()
    }


    fun setName( name: String){
        this.name = name
    }
    fun getName(): String?{
        return this.name
    }
    fun setCuisine(cuisine: String){
        this.cuisine = cuisine
    }
    fun getCuisine(): String?{
        return this.cuisine
    }
    fun setOpeningHours(openingHours: String){
        this.openingHours = openingHours
    }
    fun getOpeningHours(): String?{
        return this.openingHours
    }
    fun setCharge(charge: String){
        this.charge = charge
    }
    fun getCharge(): String?{
        return this.charge
    }
    fun setCategory(category: String){
        this.category = category
    }
    fun getCategory(): String?{
        return this.category
    }
    fun setAddress(address: ClassAddress){
        this.address = address
    }
    fun getAddress(): ClassAddress?{
        return this.address
    }
    fun setCoordinates(coordinates: ClassCoordinates){
        this.coordinates = coordinates
    }
    fun getCoordinates(): ClassCoordinates?{
        return this.coordinates
    }

    fun setContained(contained: Boolean) {
        this.containedByTrip = contained
    }
    fun isContained(): Boolean {
        return this.containedByTrip
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(cuisine)
        parcel.writeString(openingHours)
        parcel.writeString(charge)
        parcel.writeParcelable(address, flags)
        parcel.writeParcelable(coordinates, flags)
        parcel.writeString(category)
        parcel.writeByte(if (containedByTrip) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ClassPlace> {
        override fun createFromParcel(parcel: Parcel): ClassPlace {
            return ClassPlace(parcel)
        }

        override fun newArray(size: Int): Array<ClassPlace?> {
            return arrayOfNulls(size)
        }
    }


}