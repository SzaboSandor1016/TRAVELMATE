package com.example.features.trips.presentation.mappers

import com.example.features.inspect.domain.models.AddressInspectTripDomainModel
import com.example.features.inspect.domain.models.CoordinatesInspectTripDomainModel
import com.example.features.inspect.domain.models.DayOfTripInspectTripDomainModel
import com.example.features.inspect.domain.models.PlaceInspectTripDomainModel
import com.example.features.inspect.domain.models.TripInspectTripDomainModel
import com.example.features.trips.domain.models.AddressTripsDomainModel
import com.example.features.trips.domain.models.ContributorTripsDomainModel
import com.example.features.trips.domain.models.CoordinatesTripsDomainModel
import com.example.features.trips.domain.models.DayOfTripTripsDomainModel
import com.example.features.trips.domain.models.PlaceTripsDomainModel
import com.example.features.trips.domain.models.TripIdentifierTripsDomainModel
import com.example.features.trips.domain.models.TripTripsDomainModel
import com.example.features.savetrip.domain.models.AddressSaveTripDomainModel
import com.example.features.savetrip.domain.models.ContributorSaveTripDomainModel
import com.example.features.savetrip.domain.models.CoordinatesSaveTripDomainModel
import com.example.features.savetrip.domain.models.DayOfTripSaveTripDomainModel
import com.example.features.savetrip.domain.models.PlaceSaveTripDomainModel
import com.example.features.savetrip.domain.models.SaveTripDataSaveTripDomainModel
import com.example.features.trips.presentation.models.AddressTripsPresentationModel
import com.example.features.trips.presentation.models.ContributorTripsPresentationModel
import com.example.features.trips.presentation.models.CoordinatesTripsPresentationModel
import com.example.features.trips.presentation.models.DayOfTripTripsPresentationModel
import com.example.features.trips.presentation.models.PlaceTripsPresentationModel
import com.example.features.trips.presentation.models.TripIdentifierTripsPresentationModel
import com.example.features.trips.presentation.models.TripTripsPresentationModel

//FUNCTIONS TO MAP FETCHED TRIPS FROM DATABASES
//-----------------------------------------------------------------------------------------------
//START
//-----------------------------------------------------------------------------------------------

fun TripIdentifierTripsDomainModel.toTripIdentifierPresentationModel(): TripIdentifierTripsPresentationModel {
    
    return when(this) {
        is TripIdentifierTripsDomainModel.Local -> TripIdentifierTripsPresentationModel.Local(
            uuid = this.uuid,
            title = this.title
        )
        is TripIdentifierTripsDomainModel.Remote -> TripIdentifierTripsPresentationModel.Remote(
            uuid = this.uuid,
            title = this.title,
            contributorUIDs = this.contributorUIDs,
            contributors = this.contributors.mapValues { it.value.toContributorTripsPresentationModel() },
            permissionToUpdate = this.permissionToUpdate,
            creatorUID = this.creatorUID,
            creatorUsername = this.creatorUsername
        )

        TripIdentifierTripsDomainModel.Default -> TripIdentifierTripsPresentationModel.Default
    }
}

fun ContributorTripsDomainModel.toContributorTripsPresentationModel(): ContributorTripsPresentationModel {

    return ContributorTripsPresentationModel(
        uid = this.uid,
        username = this.username,
        canUpdate = this.canUpdate,
        selected = this.selected
    )
}

fun TripIdentifierTripsPresentationModel.toTripIdentifierDomainModel(): TripIdentifierTripsDomainModel {

    return when(this) {
        is TripIdentifierTripsPresentationModel.Local -> TripIdentifierTripsDomainModel.Local(
            uuid = this.uuid,
            title = this.title
        )
        is TripIdentifierTripsPresentationModel.Remote -> TripIdentifierTripsDomainModel.Remote(
            uuid = this.uuid,
            title = this.title,
            contributorUIDs = this.contributorUIDs,
            contributors = this.contributors.mapValues { it.value.toContributorTripsPresentationModel() },
            permissionToUpdate = this.permissionToUpdate,
            creatorUID = this.creatorUID,
            creatorUsername = this.creatorUsername
        )

        TripIdentifierTripsPresentationModel.Default -> TripIdentifierTripsDomainModel.Default
    }
}

fun ContributorTripsPresentationModel.toContributorTripsPresentationModel(): ContributorTripsDomainModel {

    return ContributorTripsDomainModel(
        uid = this.uid,
        username = this.username,
        canUpdate = this.canUpdate,
        selected = this.selected
    )
}

fun TripTripsDomainModel.toTripPresentationModel(): TripTripsPresentationModel {

    return when(this) {
        is TripTripsDomainModel.Local -> TripTripsPresentationModel.Local(
            uUID = this.uUID,
            startPlace = this.startPlace.toPlaceTripsPresentationModel(),
            days = this.days.map { it.toDayOfTripTripsPresentationModel() },
            date = this.date,
            title = this.title,
            note = this.note
        )
        is TripTripsDomainModel.Remote -> TripTripsPresentationModel.Remote(
            uUID = this.uUID,
            startPlace = this.startPlace.toPlaceTripsPresentationModel(),
            days = this.days.map{ it.toDayOfTripTripsPresentationModel()},
            date = this.date,
            title = this.title,
            note = this.note
        )
        is TripTripsDomainModel.Default -> TripTripsPresentationModel.Default
    }
}

fun DayOfTripTripsDomainModel.toDayOfTripTripsPresentationModel(): DayOfTripTripsPresentationModel {

    return DayOfTripTripsPresentationModel(
        dayUUID = this.dayUUID,
        tripUUID = this.tripUUID,
        label = this.label,
        places = this.places.map { it.toPlaceTripsPresentationModel() }
    )
}

fun PlaceTripsDomainModel.toPlaceTripsPresentationModel(): PlaceTripsPresentationModel {

    return PlaceTripsPresentationModel(
        uUID = this.uUID,
        name = this.name!!,
        cuisine = this.cuisine,
        openingHours = this.openingHours,
        charge = this.charge,
        address = this.address.toAddressPresentationModel(),
        coordinates = this.coordinates.toCoordinatesTripsPresentationModel(),
        category = this.category
    )
}

fun AddressTripsDomainModel.toAddressPresentationModel(): AddressTripsPresentationModel {

    return AddressTripsPresentationModel(
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        country = this.country
    )
}

fun CoordinatesTripsDomainModel.toCoordinatesTripsPresentationModel(): CoordinatesTripsPresentationModel {

    return CoordinatesTripsPresentationModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}

//FUNCTIONS TO MAP FETCHED TRIPS FROM DATABASES
//-----------------------------------------------------------------------------------------------
//END
//-----------------------------------------------------------------------------------------------

//SELECT TRIP TO INSPECT
//-----------------------------------------------------------------------------------------------
// START
//-----------------------------------------------------------------------------------------------
fun TripTripsPresentationModel.Local.toInspectTripDomainModel(): TripInspectTripDomainModel.Inspected {

    return TripInspectTripDomainModel.Inspected(
        uUID = this.uUID,
        startPlace = this.startPlace.toPlaceInspectTripDomainModel(),
        days = this.days.map{ it.toDayOfInspectedTripDomainModel() },
        date = this.date,
        title = this.title,
        note = this.note
    )
}

fun TripTripsPresentationModel.Remote.toInspectTripDomainModel(creator: String): TripInspectTripDomainModel.Inspected {

    return TripInspectTripDomainModel.Inspected(
        uUID = this.uUID,
        startPlace = this.startPlace.toPlaceInspectTripDomainModel(),
        days = this.days.map{ it.toDayOfInspectedTripDomainModel() },
        creatorUsername = creator,
        date = this.date,
        title = this.title,
        note = this.note
    )
}

fun DayOfTripTripsPresentationModel.toDayOfInspectedTripDomainModel(): DayOfTripInspectTripDomainModel {

    return DayOfTripInspectTripDomainModel(
        label = this.label,
        places = this.places.map { it.toPlaceInspectTripDomainModel() }
    )
}

fun PlaceTripsPresentationModel.toPlaceInspectTripDomainModel(): PlaceInspectTripDomainModel {

    return PlaceInspectTripDomainModel(
        uUID = this.uUID,
        name = this.name,
        cuisine = this.cuisine,
        openingHours = this.openingHours,
        charge = this.charge,
        address = this.address.toAddressInspectTripDomainModel(),
        coordinates = this.coordinates.toCoordinatesInspectTripDomainModel(),
        category = this.category
    )
}

fun AddressTripsPresentationModel.toAddressInspectTripDomainModel(): AddressInspectTripDomainModel {

    return AddressInspectTripDomainModel(
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        country = this.country
    )
}

fun CoordinatesTripsPresentationModel.toCoordinatesInspectTripDomainModel(): CoordinatesInspectTripDomainModel {

    return CoordinatesInspectTripDomainModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}



fun createSaveTripDataSaveTripDomainModelFrom(
    trip: TripTripsPresentationModel.Local, 
    tripIdentifier: TripIdentifierTripsPresentationModel.Local
): SaveTripDataSaveTripDomainModel {
    return SaveTripDataSaveTripDomainModel(
        uUID = trip.uUID,
        startPlace = trip.startPlace.toPlaceSaveTripDomainModel(null),
        daysOfTrip = trip.days.map{ it.toDayOfTripSaveTripDomainModel( trip.uUID)},
        creatorUID = null,
        selectedContributorsUIDs = emptyMap(),
        selectedContributorsParameters = emptyMap(),
        numberOfDays = trip.days.size,
        date = trip.date,
        title = trip.title,
        note = trip.note
    )
}

fun createSaveTripDataSaveTripDomainModelFrom(
    trip: TripTripsPresentationModel.Remote,
    tripIdentifier: TripIdentifierTripsPresentationModel.Remote
): SaveTripDataSaveTripDomainModel {
    return SaveTripDataSaveTripDomainModel(
        uUID = trip.uUID,
        startPlace = trip.startPlace.toPlaceSaveTripDomainModel(null),
        daysOfTrip = trip.days.map { it.toDayOfTripSaveTripDomainModel( trip.uUID) },
        creatorUID = tripIdentifier.creatorUID,
        selectedContributorsUIDs = tripIdentifier.contributorUIDs,
        selectedContributorsParameters = tripIdentifier.contributors.mapValues {
            it.value.toContributorSaveTripDomainModel()
        },
        numberOfDays = trip.days.size,
        date = trip.date,
        title = trip.title,
        note = trip.note
    )
}

fun ContributorTripsPresentationModel.toContributorSaveTripDomainModel(): ContributorSaveTripDomainModel{
    
    return ContributorSaveTripDomainModel(
        uid = this.uid,
        username = this.username,
        canUpdate = this.canUpdate,
        selected = this.selected
    )
}

fun DayOfTripTripsPresentationModel.toDayOfTripSaveTripDomainModel( tripUUID: String): DayOfTripSaveTripDomainModel {

    return DayOfTripSaveTripDomainModel(
        dayUUID = this.dayUUID,
        tripUUID = tripUUID,
        label = this.label,
        places = this.places.map{ it.toPlaceSaveTripDomainModel(this.dayUUID)}
    )
}

fun PlaceTripsPresentationModel.toPlaceSaveTripDomainModel(dayOfTripUUID: String?): PlaceSaveTripDomainModel {
    
    return PlaceSaveTripDomainModel(
        dayOfTripUUID = dayOfTripUUID,
        uUID = this.uUID,
        name = this.name,
        cuisine = this.cuisine,
        openingHours = this.openingHours,
        charge = this.charge,
        address = this.address.toAddressSaveTripDomainModel(),
        coordinates = this.coordinates.toCoordinatesSaveTripDomainModel(),
        category = this.category
    )
}

fun AddressTripsPresentationModel.toAddressSaveTripDomainModel(): AddressSaveTripDomainModel{

    return AddressSaveTripDomainModel(
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        country = this.country
    )
}
fun CoordinatesTripsPresentationModel.toCoordinatesSaveTripDomainModel(): CoordinatesSaveTripDomainModel {

    return CoordinatesSaveTripDomainModel(
        latitude= this.latitude,
        longitude = this.longitude
    )
}