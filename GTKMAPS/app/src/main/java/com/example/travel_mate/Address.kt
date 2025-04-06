package com.example.travel_mate

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude

/** [com.example.travel_mate.Address]
 * The address of a place
 * It serves as an entity (a table) for the [Room] local database
 * has
 * - an addressID (required for [Room])
 * - a city [String]
 * - a street [String]
 * - a house number [String]
 * - a country [String]
 * attribute and [get] and [set] methods for each and one to retrieve the full address as a [String]
 */
@Entity(tableName = "addresses")
class Address() : android.os.Parcelable{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "addressId") private var addressId: Int? = null
    @ColumnInfo(name = "city") private var city: String? = null
    @ColumnInfo(name = "street") private var street: String? = null
    @ColumnInfo(name = "house_number") private var houseNumber: String? = null
    @ColumnInfo(name = "country") private var country: String? = null

    constructor(parcel: Parcel) : this() {
        city = parcel.readString()
        street = parcel.readString()
        houseNumber = parcel.readString()
        country = parcel.readString()
    }
    constructor(city: String, street: String, houseNumber: String, country: String, addressId: Int) : this() {
        this.addressId = addressId
        this.city = city
        this.street = street
        this.houseNumber = houseNumber
        this.country = country
    }
    constructor(addressId: Int,city: String, street: String, houseNumber: String, country: String) : this() {
        this.addressId = addressId
        this.city = city
        this.street = street
        this.houseNumber = houseNumber
        this.country = country
    }
    fun setAddressId(addressId: Int){
        this.addressId = addressId
    }
    fun getAddressId(): Int?{
        return this.addressId
    }
    fun setCity(city: String?){
        this.city = city
    }
    fun getCity(): String?{
        return this.city
    }
    fun setStreet(street: String?){
        this.street = street
    }
    fun getStreet(): String?{
        return this.street
    }
    fun setHouseNumber(houseNumber: String?){
        this.houseNumber = houseNumber
    }
    fun getHouseNumber(): String?{
        return this.houseNumber
    }
    fun setCountry(country: String?){
        this.country = country
    }
    fun getCountry(): String?{
        return this.country
    }

    @Ignore
    @Exclude
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
    @Ignore
    @Exclude
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

    companion object CREATOR : Parcelable.Creator<Address> {
        override fun createFromParcel(parcel: Parcel): Address {
            return Address(parcel)
        }

        override fun newArray(size: Int): Array<Address?> {
            return arrayOfNulls(size)
        }
    }

}