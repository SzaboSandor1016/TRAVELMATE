package com.example.features.search.domain.mappers

import android.util.Log
import com.example.features.search.domain.models.searchmodels.AddressSearchDomainModel
import com.example.features.search.domain.models.searchmodels.CoordinatesSearchDomainModel
import com.example.features.search.domain.models.searchmodels.PlaceSearchDomainModel
import com.example.features.search.domain.models.searchmodels.SearchDataSearchDomainModel
import com.example.features.search.domain.models.searchmodels.PlaceDataSearchDomainModel
import com.example.features.search.domain.models.searchmodels.SearchStartInfoSearchDomainModel
import com.example.features.search.domain.models.searchmodels.SearchStateSearchDomainModel
import com.example.features.search.domain.models.searchoptionsmodels.SearchOptionsInfoSearchDomainModel
import com.example.features.search.domain.models.searchoptionsmodels.SearchOptionsStateSearchDomainModel
import com.example.remotedatasources.responses.OverpassResponse
import com.example.remotedatasources.responses.ReverseGeoCodeResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

fun ReverseGeoCodeResponse.mapToSearchStartPlace(): List<PlaceSearchDomainModel> {

    val places: ArrayList<PlaceSearchDomainModel> = ArrayList()

    for (feature in this.features) {

        val coordinates = CoordinatesSearchDomainModel(
            feature.geometry?.coordinates[1]?.toDouble() ?: 0.0,
            feature.geometry?.coordinates[0]?.toDouble() ?: 0.0
        )
        val address = AddressSearchDomainModel(
            feature.properties.locality,
            feature.properties.street,
            feature.properties.houseNumber,
            feature.properties.country
        )

        /*if (feature.geometry != null) {

            val geometry = feature.geometry

            if (geometry != null) {

                val coordinates = geometry.coordinates

                if (coordinates != null) {

                    placeCoordinates = CoordinatesCustomPlaceDomainModel(
                        latitude = coordinates[1].toDouble(),
                        longitude = coordinates[0].toDouble()
                    )
                }
            }

        }*/
        /*if (feature.properties != null) {

            val properties = feature.properties

            val name: String

            val city: String

            if (properties.name != null) {
                startPlace.setName(properties.name)
                Log.d("Address", properties.name.toString())
            }
            if (properties.locality != null) {
                city = properties.locality
                Log.d("Address_city", properties.locality.toString())
            }
            if (properties.street != null) {
                address.setStreet(properties.street)
                Log.d("Address_street", properties.street.toString())
            }
            if (properties.houseNumber != null) {
                address.setHouseNumber(properties.houseNumber)
                Log.d("Address_hn", properties.houseNumber.toString())
            }
            if (properties.country != null) {
                address.setCountry(properties.country)
            }

        }*/

        /*startPlace.setUUId()
        startPlace.setAddress(address)
        startPlace.setCoordinates(placeCoordinates)*/

        val startPlace = PlaceSearchDomainModel(
            uUID = UUID.randomUUID().toString(),
            name = feature.properties.name,
            address = address,
            coordinates = coordinates,
            cuisine = null,
            openingHours = null,
            charge = null,
            category = "start"
        )

        places.add(startPlace)


    }
    return places
}

/** [processOverpassResponse]
 * process the response of an Overpass network request
 * create a [List] of [com.example.model.Place]s from the [com.example.travel_mate.data.OverpassResponse]
 */
fun OverpassResponse.mapToSearchPlace(category: String): List<PlaceSearchDomainModel> {

    var places: List<PlaceSearchDomainModel> = emptyList()

    for (element in elements) {

        //place.setUUId()

        val coordinates = if (element.center != null) {
            CoordinatesSearchDomainModel(element.center.lat, element.center.lon)
        } else {
            CoordinatesSearchDomainModel(element.lat, element.lon)
        }

        //place.setCategory(category)
        val tags = element.tags ?: emptyMap()

        /*place.setName(

            when {
                tags.containsKey("name") -> tags["name"]!!
                else -> "Ismeretlen"
            }
        )*/
        val address = AddressSearchDomainModel(
            tags["addr:city"],
            tags["addr:street"],
            tags["addr:housenumber"],
            null
        )

        /*if (tags.containsKey("cuisine")) place.setCuisine(tags["cuisine"]!!)
        if (tags.containsKey("opening_hours")) place.setOpeningHours(tags["opening_hours"]!!)
        if (tags.containsKey("charge")) place.setCharge(tags["charge"]!!)
        if (tags.containsKey("addr:city")) address.setCity(tags["addr:city"]!!)
        if (tags.containsKey("addr:street")) address.setStreet(tags["addr:street"]!!)
        if (tags.containsKey("addr:housenumber")) address.setHouseNumber(tags["addr:housenumber"]!!)*/

        //Log.d("name", place.getName()!!)

        val place = PlaceSearchDomainModel(
            uUID = UUID.randomUUID().toString(),
            name = tags["name"],
            cuisine = tags["cuisine"],
            openingHours = tags["opening_hours"],
            charge =tags["charge"],
            address = address,
            coordinates = coordinates,
            category = category
        )

        places = places.plus(place)
    }

    return places
}

fun StateFlow<SearchStateSearchDomainModel>.toFlowOfSearchInfoDomainModel(): Flow<SearchStartInfoSearchDomainModel> {

    return this.map {

        SearchStartInfoSearchDomainModel(
            name = it.search.getStartPlace()?.getName(),
            city =  it.search.getStartPlace()?.getAddress()?.city,
            street = it.search.getStartPlace()?.getAddress()?.street,
            houseNumber = it.search.getStartPlace()?.getAddress()?.houseNumber,
            country = it.search.getStartPlace()?.getAddress()?.country
        )
    }
}

fun StateFlow<SearchStateSearchDomainModel>.toFlowOfSearchPlaceDataDomainModel(): Flow<PlaceDataSearchDomainModel?> {

    return this.map {

        val start = it.search.getStartPlace()

        if (start != null)
        PlaceDataSearchDomainModel(
            uUID = start.getUUID(),
            name = start.getName(),
            coordinates = start.getCoordinates(),
            category = start.getCategory()
        )
        else null
    }
}

fun StateFlow<SearchStateSearchDomainModel>.toFlowOfSearchDataDomainModel(): Flow<SearchDataSearchDomainModel> {

    return this.map {

        SearchDataSearchDomainModel(
            places = it.search.getPlaces().map { it.toPlaceDataSearchDomainModel() }
        )
    }
}

fun PlaceSearchDomainModel.toPlaceDataSearchDomainModel(): PlaceDataSearchDomainModel {
    return PlaceDataSearchDomainModel(
        uUID = this.getUUID(),
        name = this.getName(),
        coordinates = this.getCoordinates(),
        category = this.getCategory()
    )
}

fun StateFlow<SearchOptionsStateSearchDomainModel>.toFlowOfSearchOptionsInfoDomainModel():
        Flow<SearchOptionsInfoSearchDomainModel> {
        return this.map {
            SearchOptionsInfoSearchDomainModel(
                parametersSelected = it.distance != 0.0,
                transportSelected = !it.transportMode.equals(null)
            )
        }
}