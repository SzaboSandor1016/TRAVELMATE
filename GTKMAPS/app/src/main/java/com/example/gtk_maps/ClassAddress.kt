package com.example.gtk_maps

import android.os.Parcel
import android.os.Parcelable

class ClassAddress() : android.os.Parcelable{
    private var city: String? = null
    private var street: String? = null
    private var houseNumber: String? = null
    private var country: String? = null

    constructor(parcel: Parcel) : this() {
        city = parcel.readString()
        street = parcel.readString()
        houseNumber = parcel.readString()
        country = parcel.readString()
    }

    fun setCity(city: String){
        this.city = city
    }
    fun getCity(): String?{
        return this.city
    }

    fun setStreet(street: String){
        this.street = street
    }

    fun getStreet(): String?{
        return this.street
    }

    fun setHouseNumber(houseNumber: String){
        this.houseNumber = houseNumber
    }

    fun getHouseNumber(): String?{
        return this.houseNumber
    }

    fun setCountry(country: String){
        this.country = country
    }
    fun getCountry(): String?{
        return this.country
    }

    fun getAddress(): String? {
        var address: String = ""
        if (this.city != null && this.street != null && this.houseNumber != null){
            address += this.city + ", " + this.street + " " + this.houseNumber

            return  address
        }
        if (this.city != null && this.street != null){
            address+= this.city + ", " + this.street

            return  address
        }

        return  null
    }
    fun getFullAddress(): String {
        var address = ""
        if (this.city != null) address += this.city
        if (this.street != null) address += ", " + this.street
        if (this.houseNumber != null) address += " " + this.houseNumber
        if (this.country != null) address+= " " + this.country

        return address
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(city)
        parcel.writeString(street)
        parcel.writeString(houseNumber)
        parcel.writeString(country)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ClassAddress> {
        override fun createFromParcel(parcel: Parcel): ClassAddress {
            return ClassAddress(parcel)
        }

        override fun newArray(size: Int): Array<ClassAddress?> {
            return arrayOfNulls(size)
        }
    }

}