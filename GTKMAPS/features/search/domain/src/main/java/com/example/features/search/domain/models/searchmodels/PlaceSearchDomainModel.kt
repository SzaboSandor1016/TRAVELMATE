package com.example.features.search.domain.models.searchmodels

import java.util.UUID

data class PlaceSearchDomainModel(
    private val uUID: String,
    private val name: String?,
    private val cuisine: String?,
    private val openingHours: String?,
    private val charge: String?,
    private val address: AddressSearchDomainModel,
    private val coordinates: CoordinatesSearchDomainModel,
    private val category: String,
/*    private val containedByTrip: Boolean = false,
    private val containedByRoute: Boolean = false*/
) {
    constructor(
        address: AddressSearchDomainModel,
        coordinates: CoordinatesSearchDomainModel
    ): this(
        uUID = UUID.randomUUID().toString(),
        name = "",
        cuisine = "",
        openingHours = "",
        charge = "",
        address = address,
        coordinates = coordinates,
        category = ""
    )

    fun setUUId(): PlaceSearchDomainModel {

        return this.copy(
            uUID = UUID.randomUUID().toString()
        )
    }

    fun getUUID(): String {
        return this.uUID
    }
    fun setName(name: String): PlaceSearchDomainModel{
        return this.copy( name = name)
    }
    fun getName(): String?{
        return this.name
    }
    fun setCuisine(cuisine: String?): PlaceSearchDomainModel{
        return this.copy( cuisine = cuisine)
    }
    fun getCuisine(): String?{
        return this.cuisine
    }
    fun setOpeningHours(openingHours: String?): PlaceSearchDomainModel{
        return this.copy(openingHours = openingHours)
    }
    fun getOpeningHours(): String?{
        return this.openingHours
    }
    fun setCharge(charge: String?): PlaceSearchDomainModel{
        return this.copy(charge = charge)
    }
    fun getCharge(): String?{
        return this.charge
    }
    fun setCategory(category: String): PlaceSearchDomainModel{
        return this.copy(category = category)
    }
    fun getCategory(): String{
        return this.category
    }
    fun setAddress(address: AddressSearchDomainModel): PlaceSearchDomainModel{
        return this.copy(address = address)
    }
    fun getAddress(): AddressSearchDomainModel{
        return this.address
    }
    fun setCoordinates(coordinates: CoordinatesSearchDomainModel): PlaceSearchDomainModel{
        return this.copy(coordinates = coordinates)
    }
    fun getCoordinates(): CoordinatesSearchDomainModel {
        return this.coordinates
    }
    /*fun setContainedByTrip(contained: Boolean): PlaceSearchDomainModel {
        return this.copy(containedByTrip = contained)
    }
    fun isContainedByTrip(): Boolean {
        return this.containedByTrip
    }
    fun setContainedByRoute(contained: Boolean): PlaceSearchDomainModel {

        return this.copy(containedByRoute = contained)
    }

    fun isContainedByRoute(): Boolean {
        return this.containedByRoute
    }*/
}