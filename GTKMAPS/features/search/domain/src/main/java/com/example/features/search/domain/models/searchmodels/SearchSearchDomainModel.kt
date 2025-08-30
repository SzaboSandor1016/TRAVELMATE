package com.example.features.search.domain.models.searchmodels

/** [SearchSearchDomainModel]
 * a class for a search made with the app
 * has a [startPlace] and a [List] containing every found nearby [com.example.model.Place]s
 *
 * has several functions too, I won't list them, their names speak themselves
 */
data class SearchSearchDomainModel(private var startPlace: PlaceSearchDomainModel? = null,
                                   private var places: List<PlaceSearchDomainModel> = emptyList()) /*: android.os.Parcelable*/ {

    /*private val transportModeSpeeds: Map<String, Int> =
        mapOf( "walk" to 3500,
                "car" to 40000)*/

    //private var startPlace: ClassPlace? = null
    /*private var distance: Double?= null
    private var transportMode: String? = null
    private var minute: Int? = null*/

    /*constructor(parcel: Parcel) : this() {
        startPlace = parcel.readParcelable(ClassPlace::class.java.classLoader)
        *//*distance = parcel.readValue(Double::class.java.classLoader) as? Double
        transportMode = parcel.readString()
        minute = parcel.readValue(Int::class.java.classLoader) as? Int*//*
    }*/


    fun setStartPlace(startPlace: PlaceSearchDomainModel?): SearchSearchDomainModel{
        return this.copy(
            startPlace = startPlace,
            places = emptyList()
        )
    }
    fun getStartPlace(): PlaceSearchDomainModel?{
        return this.startPlace
    }
    fun setPlaces(places: List<PlaceSearchDomainModel>): SearchSearchDomainModel{
        return this.copy(places = places)
    }
    fun getPlaces(): List<PlaceSearchDomainModel>{
        return places
    }
    /*fun setDistance(distance: Double?){
        this.distance = distance
    }
    fun getDistance(): Double?{
        return this.distance
    }*/
    fun addPlaces(places: List<PlaceSearchDomainModel>): SearchSearchDomainModel {
        return this.copy(places = this.places.plus(places))
    }
    fun removePlaces(places: ArrayList<PlaceSearchDomainModel>): SearchSearchDomainModel{
        return this.copy(places = this.places.minus(places.toSet()))
    }
    fun addPlace(place: PlaceSearchDomainModel): SearchSearchDomainModel{
        return this.copy(places = this.places.plus(place))
    }
    fun removePlace(place: PlaceSearchDomainModel): SearchSearchDomainModel{
        return this.copy(places = this.places.minus(place))
    }

    fun getPlaceByUUID( uuid: String): PlaceSearchDomainModel? {

        return places.find { it.getUUID() == uuid }
    }

    /*fun selectPlaceByUUIDForTrip(uuid: String): SearchSearchDomainModel {

        return this.copy(
            places = places.map { place ->
                if (place.getUUID() == uuid) place.setContainedByTrip(!place.isContainedByTrip()) else place
            }
        )
    }

    fun selectPlaceByUUIDForRoute(uuid: String): SearchSearchDomainModel {

        return this.copy(
            places = places.map { place ->
                if (place.getUUID() == uuid) place.setContainedByRoute(!place.isContainedByRoute()) else place
            }
        )
    }

    fun getPlacesContainedByTrip(): List<PlaceSearchDomainModel> {

        return places.filter { it.isContainedByTrip() }
    }*/

    /*fun setTransportMode(transportMode: String?){
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
    }*/

    /*fun hasPlaceAddedToTrip(): Boolean {

        return this.places.any {
            it.isContainedByTrip()
        }
    }

    fun clearPlacesAddedToTrip(): SearchSearchDomainModel {
        return this.copy(
            places = this.places.map {
                it.setContainedByTrip(contained = false)
            }
        )
    }

    fun hasPlaceAddedToRoute(): Boolean {

        return this.places.any {
            it.isContainedByRoute()
        }
    }

    fun clearPlacesAddedToRoute(): SearchSearchDomainModel {

        return this.copy(
            places = this.places.map { it.setContainedByRoute(contained = false) }
        )
    }*/

    /*override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(startPlace, flags)
        *//*parcel.writeValue(distance)
        parcel.writeString(transportMode)
        parcel.writeValue(minute)*//*
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
    }*/

}