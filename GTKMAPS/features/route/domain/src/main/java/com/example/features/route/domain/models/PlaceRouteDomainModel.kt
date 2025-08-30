package com.example.features.route.domain.models

import java.util.UUID

data class PlaceRouteDomainModel(
    val uUID: String,
    val name: String?,
   /* val cuisine: String?,
    val openingHours: String?,
    val charge: String?,
    val address: AddressRouteDomainModel,*/
    val coordinates: CoordinatesRouteDomainModel,
    //val category: String,
) {
    constructor(
        //address: AddressRouteDomainModel,
        coordinates: CoordinatesRouteDomainModel
    ): this(
        uUID = UUID.randomUUID().toString(),
        name = "",
       /* cuisine = "",
        openingHours = "",
        charge = "",
        address = address,*/
        coordinates = coordinates,
        //category = ""
    )


    /*fun getUUID(): String {
        return this.uUID
    }
    fun setUUId(uuid: String) {

        this.uUID = uuid
    }
    fun setUUId(): PlaceRouteDomainModel {
        return this.copy(uUID = UUID.randomUUID().toString())
    }
    fun setName(name: String): PlaceRouteDomainModel{
        return this.copy( name = name)
    }
    fun setName(name: String){
        this.name = name
    }
    fun getName(): String?{
        return this.name
    }
    fun setCuisine(cuisine: String?): PlaceRouteDomainModel{
        return this.copy( cuisine = cuisine)
    }
    fun setCuisine(cuisine: String?){
        this.cuisine = cuisine
    }
    fun getCuisine(): String?{
        return this.cuisine
    }
    fun setOpeningHours(openingHours: String?): PlaceRouteDomainModel{
        return this.copy(openingHours = openingHours)
    }
    fun setOpeningHours(openingHours: String?){
        this.openingHours = openingHours
    }
    fun getOpeningHours(): String?{
        return this.openingHours
    }
    fun setCharge(charge: String?): PlaceRouteDomainModel{
        return this.copy(charge = charge)
    }
    fun setCharge(charge: String?){
        this.charge = charge
    }
    fun getCharge(): String?{
        return this.charge
    }
    fun setCategory(category: String): PlaceRouteDomainModel{
        return this.copy(category = category)
    }
    fun setCategory(category: String?){
        this.category = category
    }
    fun getCategory(): String{
        return this.category
    }
    fun setAddress(address: AddressRouteDomainModel): PlaceRouteDomainModel{
        return this.copy(address = address)
    }
    fun setAddress(address: AddressRouteDomainModel?){
        this.address = address
    }
    fun getAddress(): AddressRouteDomainModel{
        return this.address
    }
    fun setCoordinates(coordinates: CoordinatesRouteDomainModel): PlaceRouteDomainModel{
        return this.copy(coordinates = coordinates)
    }
    fun setCoordinates(coordinates: CoordinatesRouteDomainModel){
        this.coordinates = coordinates
    }
    fun getCoordinates(): CoordinatesRouteDomainModel {
        return this.coordinates
    }*/

}