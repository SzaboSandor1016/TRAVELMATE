package com.example.travel_mate.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.travel_mate.data.Address
import com.example.travel_mate.data.Coordinates
import com.google.firebase.database.Exclude
import java.io.Serializable
import java.util.UUID

/** [Place]
 * The class stores the data of a found place
 * It also serves as an [androidx.room.Entity] object for the [Room] local database
 *
 * It has may properties I'll better not list them.
 * All of them has [com.example.travel_mate.get] and [set] methods too
 */
@Entity(tableName = "places")
data class Place(
    @PrimaryKey(autoGenerate = false) var uUID: String,
    @ColumnInfo(name = "trip_id")
    @Exclude private var tripId: String? = null,
    @ColumnInfo(name = "place_name") private var name: String? = null,
    @ColumnInfo(name = "cuisine") private var cuisine: String? = null,
    @ColumnInfo(name = "opening_hours")private var openingHours: String? = null,
    @ColumnInfo(name = "charge")private var charge: String? = null,
    @Embedded private var address: Address? = null,
    @Embedded private var coordinates: Coordinates = Coordinates(),
    @ColumnInfo(name = "category") private var category: String? = null,
    @ColumnInfo(name = "contained_by_trip") private var containedByTrip: Boolean = false,
    @Exclude
    @Ignore private var containedByRoute: Boolean = false) : Serializable {

    constructor(): this(
        uUID = UUID.randomUUID().toString()
    )

    constructor(place: Place): this() {
        this.uUID = place.uUID
        this.name = place.name
        this.charge = place.charge
        this.address = place.address
        this.cuisine = place.cuisine
        this.category = place.category
        this.containedByTrip = place.containedByTrip
        this.containedByRoute = place.containedByRoute
        this.coordinates = place.coordinates
        this.openingHours = place.openingHours
    }

    constructor(
        uuid: String,
        name: String,
        charge: String,
        address: Address,
        cuisine: String,
        category: String,
        containedByTrip: Boolean,
        containedByRoute: Boolean,
        coordinates: Coordinates,
        openingHours: String
    ): this() {
        this.uUID = uuid
        this.name = name
        this.charge = charge
        this.address = address
        this.cuisine = cuisine
        this.category = category
        this.containedByTrip = containedByTrip
        this.containedByRoute = containedByRoute
        this.coordinates = coordinates
        this.openingHours = openingHours
    }
    fun getTripId(): String? {
        return this.tripId
    }
    fun setTripId(tripId: String?) {

        this.tripId = tripId
    }


    /*fun getUUID(): String {
        return this.uUID
    }
    fun setUUId(uuid: String) {

        this.uUID = uuid
    }*/
    fun setUUId() {
        this.uUID = UUID.randomUUID().toString()
    }
    fun name(name: String): Place{
        return this.copy( name = name)
    }
    fun setName(name: String){
        this.name = name
    }
    fun getName(): String?{
        return this.name
    }
    fun cuisine(cuisine: String?): Place{
        return this.copy( cuisine = cuisine)
    }
    fun setCuisine(cuisine: String?){
        this.cuisine = cuisine
    }
    fun getCuisine(): String?{
        return this.cuisine
    }
    fun openingHours(openingHours: String?): Place{
        return this.copy(openingHours = openingHours)
    }
    fun setOpeningHours(openingHours: String?){
        this.openingHours = openingHours
    }
    fun getOpeningHours(): String?{
        return this.openingHours
    }
    fun charge(charge: String?): Place{
        return this.copy(charge = charge)
    }
    fun setCharge(charge: String?){
        this.charge = charge
    }
    fun getCharge(): String?{
        return this.charge
    }
    fun category(category: String?): Place{
        return this.copy(category = category)
    }
    fun setCategory(category: String?){
        this.category = category
    }
    fun getCategory(): String?{
        return this.category
    }
    fun address(address: Address?): Place{
        return this.copy(address = address)
    }
    fun setAddress(address: Address?){
        this.address = address
    }
    fun getAddress(): Address?{
        return this.address
    }
    fun coordinates(coordinates: Coordinates): Place{
        return this.copy(coordinates = coordinates)
    }
    fun setCoordinates(coordinates: Coordinates){
        this.coordinates = coordinates
    }
    fun getCoordinates(): Coordinates {
        return this.coordinates
    }
    fun containedByTrip(contained: Boolean): Place {
        return this.copy(containedByTrip = contained)
    }
    fun setContainedByTrip(contained: Boolean) {
        this.containedByTrip = contained
    }
    fun isContainedByTrip(): Boolean {
        return this.containedByTrip
    }
    fun containedByRoute(contained: Boolean): Place {

        return this.copy(containedByRoute = contained)
    }
    fun setContainedByRoute(contained: Boolean) {
        this.containedByRoute = contained
    }

    fun isContainedByRoute(): Boolean {
        return this.containedByRoute
    }

}