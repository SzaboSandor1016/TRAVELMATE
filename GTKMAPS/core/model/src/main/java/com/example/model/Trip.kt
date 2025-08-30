package com.example.model

import androidx.room.Ignore
import java.io.Serializable
import java.util.UUID

@Entity(tableName = "trips")
@IgnoreExtraProperties
data class Trip(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    @get:Exclude var uUID: String,
    @Embedded var startPlace: Place,
    @Ignore var places: List<Place>,
    /*private var creatorUID: String? = null
    private var contributors: Map<String, Boolean> = hashMapOf()*/
    @ColumnInfo(name = "date_of_trip") var date: String?,
    @ColumnInfo(name = "trip_title") var title: String?,
    @ColumnInfo(name = "trip_note") var note: String?,
): Serializable {

    constructor(): this(
        uUID = UUID.randomUUID().toString(),
        startPlace = Place(),
        places = emptyList<Place>(),
        date = "",
        title = "",
        note = ""
    )

    fun findPlaceByUUID(uUID: String): Place? {

        return places.find { it.uUID == uUID }
    }

    fun selectInspectedPlaceByUUIDForRoute(uuid: String): Trip {

        return this.copy(
            places = places.map { place ->
                if (place.uUID == uuid) place.containedByRoute(!place.isContainedByRoute()) else place
            }
        )
    }

    /*fun setUUID(uuid: String) {

        this.uUID = uuid
    }
    fun getUUID(): String {
        return this.uUID
    }*/
    /*fun setStartPlace(startPlace: Place){
        this.startPlace = startPlace
    }
    fun getStartPlace(): Place?{
        return this.startPlace
    }
    fun setPlaces(places: List<Place>){
        this.places = places
    }
    fun getPlaces(): List<Place>?{
        return this.places
    }
    fun addPlace(place: Place){
        //this.places?.add(place)
        this.places = this.places.plus(place)
    }
    fun removePlace(place: Place){
        //this.places?.remove(place)
        this.places = this.places.minus(place)
    }
    fun setDate(date: String){
        this.date = date
    }
    fun getDate(): String?{
        return this.date
    }
    fun setTitle(title: String){
        this.title = title
    }
    fun getTitle(): String?{
        return this.title
    }
    fun setNote(note: String){
        this.note = note
    }
    fun getNote(): String?{
        return this.note
    }*/
    /*fun getContributors(): Map<String, Boolean> {

        return this.contributors
    }
    fun setContributors(contributors: Map<String,Any>) {
        this.contributors = contributors.mapValues { true }
    }

    fun setCreatorUID(uid: String) {
        this.creatorUID = uid
    }
    fun getCreatorUID(): String? {
        return this.creatorUID
    }*/
    /*fun isPlaceContained(place: Place): Boolean{
        return this.places.contains(place)
    }
    *//*fun isPlacesEmpty(): Boolean{
        return this.places.isEmpty()
    }*//*
    fun sizeOfPlaces(): Int{
        return places!!.size
    }
    fun clear(){
        //this.contributors = hashMapOf()
        this.startPlace = null
        this.places = emptyList()
        this.note = ""
        this.title = ""
        this.date = ""
    }*/


}