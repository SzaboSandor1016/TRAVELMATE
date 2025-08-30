package com.example.features.trips.data.mappers

import android.util.Log
import com.example.core.remotedatasources.tripremotedatasource.domain.models.AddressRemoteEntityModel
import com.example.core.remotedatasources.tripremotedatasource.domain.models.ContributorRemoteEntityModel
import com.example.core.remotedatasources.tripremotedatasource.domain.models.CoordinatesRemoteEntityModel
import com.example.core.remotedatasources.tripremotedatasource.domain.models.DayOfTripRemoteEntityModel
import com.example.core.remotedatasources.tripremotedatasource.domain.models.PlaceRemoteEntityModel
import com.example.core.remotedatasources.tripremotedatasource.domain.models.TripIdentifierRemoteEntityModel
import com.example.core.remotedatasources.tripremotedatasource.domain.models.TripRemoteEntityModel
import com.example.core.databse.dao.TripDao
import com.example.core.databse.dao.models.AddressLocalEntityModel
import com.example.core.databse.dao.models.CoordinatesLocalEntityModel
import com.example.core.databse.dao.models.PlaceLocalEntityModel
import com.example.core.databse.dao.models.TripIdentifierLocalEntityModel
import com.example.features.trips.domain.models.AddressTripsDomainModel
import com.example.features.trips.domain.models.ContributorTripsDomainModel
import com.example.features.trips.domain.models.CoordinatesTripsDomainModel
import com.example.features.trips.domain.models.DayOfTripTripsDomainModel
import com.example.features.trips.domain.models.PlaceTripsDomainModel
import com.example.features.trips.domain.models.TripIdentifierTripsDomainModel
import com.example.features.trips.domain.models.TripTripsDomainModel

fun TripDao.TripWithDaysAndPlaces.toTripTripsDomainModel(): TripTripsDomainModel.Local {

    Log.d("localTripMapper", "Number of Days: ${this.days.size} ")

    return TripTripsDomainModel.Local(
        uUID = this.trip.uUID,
        startPlace = this.trip.startPlace.toPlaceTripsDomainModel(),
        days = this.days.map { it.toDayWithPlacesTripsDomainModel() },
        date = this.trip.date,
        title = this.trip.title,
        note = this.trip.note
    )
}

fun TripDao.DayWithPlaces.toDayWithPlacesTripsDomainModel(): DayOfTripTripsDomainModel {

    Log.d("localTripMapper", "Number of places for day with UUID ${this.day.dayUUID} : ${this.places.size}")

    return DayOfTripTripsDomainModel(
        dayUUID = this.day.dayUUID,
        tripUUID = this.day.tripId,
        label = this.day.label,
        places = this.places.map{ it.toPlaceTripsDomainModel() }
    )
}

/*fun TripLocalEntityModel.toTripTripsDomainModel(days: List<DayOfTripTripsDomainModel>): TripTripsDomainModel.Local {
    return TripTripsDomainModel.Local(
        uUID = this.uUID,
        startPlace = this.startPlace.toPlaceTripsDomainModel(),
        //places = this.places.map { it.toPlaceTripsDomainModel() },
        days = days,
        date = this.date,
        title = this.title,
        note = this.note,
    )
}*/

fun PlaceLocalEntityModel.toPlaceTripsDomainModel(): PlaceTripsDomainModel {
    
    return PlaceTripsDomainModel(
        dayOfTripUUID = this.dayId,
        uUID = this.uUID,
        name = this.name,
        cuisine = this.cuisine,
        openingHours = this.openingHours,
        charge = this.charge,
        address = this.address.toAddressTripsDomainModel(),
        coordinates = this.coordinates.toCoordinatesTripsDomainModel(),
        category = this.category,
    )
}

fun AddressLocalEntityModel.toAddressTripsDomainModel(): AddressTripsDomainModel {

    return AddressTripsDomainModel(
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        country = this.country,
    )
}

fun CoordinatesLocalEntityModel.toCoordinatesTripsDomainModel(): CoordinatesTripsDomainModel {
    return CoordinatesTripsDomainModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}

/*fun TripTripsDomainModel.Local.toTripEntityModel(): TripLocalEntityModel {
    return TripLocalEntityModel(
        uUID = this.uUID,
        startPlace = this.startPlace.toPlaceEntityModelModel(this.uUID),
        //places = this.places.map { it.toPlaceEntityModelModel() },
        date = this.date,
        title = this.title,
        note = this.note,
    )
}

fun PlaceTripsDomainModel.toPlaceEntityModelModel(dayID: String): PlaceLocalEntityModel {

    return PlaceLocalEntityModel(
        uUID = this.uUID,
        dayId = dayID,
        name = this.name,
        cuisine = this.cuisine,
        openingHours = this.openingHours,
        charge = this.charge,
        address = this.address.toAddressEntityModel(),
        coordinates = this.coordinates.toCoordinatesEntityModel(),
        category = this.category,
    )
}

fun AddressTripsDomainModel.toAddressEntityModel(): AddressLocalEntityModel {

    return AddressLocalEntityModel(
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        country = this.country,
    )
}

fun CoordinatesTripsDomainModel.toCoordinatesEntityModel(): CoordinatesLocalEntityModel {
    return CoordinatesLocalEntityModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}*/

fun TripIdentifierLocalEntityModel.toTripIdentifierTripsDomainModel(): TripIdentifierTripsDomainModel.Local {
    return TripIdentifierTripsDomainModel.Local(
        uuid = this.uuid,
        title = this.title
    )
}

/*fun TripTripsDomainModel.Local.toTripIdentifierEntityModel(): TripIdentifierEntityModel {

    return TripIdentifierEntityModel(
        uuid = this.uUID,
        title = this.title
    )
}*/

fun TripIdentifierRemoteEntityModel.toTripIdentifierTripsDomainModel(
    creatorUsername: String,
    contributors: Map<String, ContributorTripsDomainModel>,
    permission: Boolean
): TripIdentifierTripsDomainModel.Remote {
    return TripIdentifierTripsDomainModel.Remote(
        uuid = this.uuid!!,
        title = this.title!!,
        contributorUIDs = this.contributorUIDs,
        contributors = contributors,
        permissionToUpdate = permission,
        creatorUID = this.creatorUID!!,
        creatorUsername = creatorUsername
    )
}

fun ContributorRemoteEntityModel.toContributorTripDomainModel(uid: String, username: String): ContributorTripsDomainModel {
    
    return ContributorTripsDomainModel(
        uid = uid,
        username = username,
        canUpdate = this.canUpdate,
        selected = true
    )
}

fun TripRemoteEntityModel.toTripTripsDomainModel(): TripTripsDomainModel.Remote {

    return TripTripsDomainModel.Remote(
        uUID = this.uUID!!,
        startPlace = this.startPlace!!.toPlaceTripsDomainModel(),
        days = this.days.map { it.toDayOfTripTripsDomainModel() },
        date = this.date,
        title = this.title,
        note = this.note
    )
}

fun DayOfTripRemoteEntityModel.toDayOfTripTripsDomainModel(): DayOfTripTripsDomainModel {

    return DayOfTripTripsDomainModel(
        label = this.label,
        places = this.places.map { it.toPlaceTripsDomainModel() }
    )
}

fun PlaceRemoteEntityModel.toPlaceTripsDomainModel(): PlaceTripsDomainModel {

    return PlaceTripsDomainModel(
        uUID = this.uUID,
        name = this.name,
        cuisine = this.cuisine,
        openingHours = this.openingHours,
        charge = this.charge,
        address = this.address.toAddressTripsDomainModel(),
        coordinates = this.coordinates.toCoordinatesTripsDomainModel(),
        category = this.category
    )
}

fun AddressRemoteEntityModel.toAddressTripsDomainModel(): AddressTripsDomainModel {

    return AddressTripsDomainModel(
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        country = this.country
    )
}

fun CoordinatesRemoteEntityModel.toCoordinatesTripsDomainModel(): CoordinatesTripsDomainModel {

    return CoordinatesTripsDomainModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}

