package com.example.gtk_maps

import android.os.Build
import android.os.Parcel
import androidx.annotation.RequiresApi
import java.util.UUID

class ClassTrip() : android.os.Parcelable {

    private var uuid: String? = null
    private var startPlace: ClassPlace? = null
    private var places: ArrayList<ClassPlace> = ArrayList()
    private var contributors: ArrayList<String> = ArrayList()
    private var date: String = ""
    private var title: String = ""
    private var note: String = ""

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    constructor(parcel: Parcel) : this() {
        uuid = parcel.readString()
        startPlace = parcel.readParcelable(ClassPlace::class.java.classLoader)
        places = parcel.readValue(ClassPlace::class.java.classLoader) as ArrayList<ClassPlace>
        date = parcel.readString().toString()
        title = parcel.readString().toString()
        note = parcel.readString().toString()
    }

    fun setUUID(){
        this.uuid = UUID.randomUUID().toString()
    }

    fun setUUIDFromOtherTrip(uuid: String) {
        this.uuid = uuid
    }

    fun getUUID(): String? {
        return this.uuid
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
        return this.places
    }
    fun addPlace(place: ClassPlace){
        this.places.add(place)
    }
    fun removePlace(place: ClassPlace){
        this.places.remove(place)
    }
    fun setDate(date: String){
        this.date = date
    }
    fun getDate(): String{
        return this.date
    }
    fun setTitle(title: String){
        this.title = title
    }
    fun getTitle(): String{
        return this.title
    }
    fun setNote(note: String){
        this.note = note
    }
    fun getNote(): String{
        return this.note
    }
    fun addContributors(contributors: ArrayList<String>){
        this.contributors.addAll(contributors)
    }
    fun addContributor(contributor: String){
        this.contributors.add(contributor)
    }
    fun removeContributors(contributors: ArrayList<String>){
        this.contributors.removeAll(contributors.toSet())
    }
    fun removeContributor(contributor: String) {
        this.contributors.remove(contributor)
    }
    fun isPlaceContained(place: ClassPlace): Boolean{
        return this.places.contains(place)
    }
    fun isPlacesEmpty(): Boolean{
        return this.places.isEmpty()
    }
    fun getSize(): Int{
        return places.size
    }
    fun clear(){
        this.uuid = null
        this.contributors = ArrayList()
        this.startPlace = null
        this.places = ArrayList()
        this.note = ""
        this.title = ""
        this.date = ""
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uuid)
        parcel.writeParcelable(startPlace, flags)
        parcel.writeValue(places)
        parcel.writeString(date)
        parcel.writeString(title)
        parcel.writeString(note)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : android.os.Parcelable.Creator<ClassTrip> {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun createFromParcel(parcel: Parcel): ClassTrip {
            return ClassTrip(parcel)
        }

        override fun newArray(size: Int): Array<ClassTrip?> {
            return arrayOfNulls(size)
        }
    }


}