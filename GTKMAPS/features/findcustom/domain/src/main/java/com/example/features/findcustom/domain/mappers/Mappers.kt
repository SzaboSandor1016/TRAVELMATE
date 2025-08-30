package com.example.features.findcustom.domain.mappers

import com.example.features.findcustom.domain.models.AddressCustomPlaceDomainModel
import com.example.features.findcustom.domain.models.CoordinatesCustomPlaceDomainModel
import com.example.features.findcustom.domain.models.CustomPlaceInfoCustomPlaceDomainModel
import com.example.features.findcustom.domain.models.CustomPlaceMapDataCustomPlaceDomainModel
import com.example.features.findcustom.domain.models.CustomPlaceStateCustomPlaceModel
import com.example.features.findcustom.domain.models.PlaceCustomPlaceDomainModel
import com.example.remotedatasources.responses.ReverseGeoCodeResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

fun ReverseGeoCodeResponse.mapToCustomPlace(): List<PlaceCustomPlaceDomainModel.CustomPlace> {

    val places: ArrayList<PlaceCustomPlaceDomainModel.CustomPlace> = ArrayList()

    for (feature in this.features) {

        val coordinates = CoordinatesCustomPlaceDomainModel(
            feature.geometry?.coordinates[1]?.toDouble() ?: 0.0,
            feature.geometry?.coordinates[0]?.toDouble() ?: 0.0
        )
        val address = AddressCustomPlaceDomainModel(
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

        val startPlace = PlaceCustomPlaceDomainModel.CustomPlace(
            uUID = UUID.randomUUID().toString(),
            name = feature.properties.name,
            address = address,
            coordinates = coordinates,
            category = "custom"
        )

        places.add(startPlace)


    }
    return places
}

fun StateFlow<CustomPlaceStateCustomPlaceModel>.mapToInfoState(): Flow<CustomPlaceInfoCustomPlaceDomainModel> {
    return this.map {

        when(it.customPlace) {

            is PlaceCustomPlaceDomainModel.Default -> CustomPlaceInfoCustomPlaceDomainModel.Default

            is PlaceCustomPlaceDomainModel.CustomPlace -> CustomPlaceInfoCustomPlaceDomainModel.CustomPlace(
                uUID = it.customPlace.uUID,
                name = it.customPlace.name,
                address = it.customPlace.address
            )
        }
    }
}

fun StateFlow<CustomPlaceStateCustomPlaceModel>.mapToMapData(): Flow<CustomPlaceMapDataCustomPlaceDomainModel> {

    return this.map {

        when(it.customPlace) {

            is PlaceCustomPlaceDomainModel.Default -> CustomPlaceMapDataCustomPlaceDomainModel.Default

            is PlaceCustomPlaceDomainModel.CustomPlace -> CustomPlaceMapDataCustomPlaceDomainModel.CustomPlace(
                uuid = it.customPlace.uUID,
                name = it.customPlace.name,
                coordinates = it.customPlace.coordinates,
                category = it.customPlace.category
            )
        }
    }
}