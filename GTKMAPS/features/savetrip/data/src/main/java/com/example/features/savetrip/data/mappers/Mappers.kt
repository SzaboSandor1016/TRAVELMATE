package com.example.features.savetrip.data.mappers

import com.example.core.remotedatasources.tripremotedatasource.domain.models.AddressRemoteEntityModel
import com.example.core.remotedatasources.tripremotedatasource.domain.models.ContributorRemoteEntityModel
import com.example.core.remotedatasources.tripremotedatasource.domain.models.CoordinatesRemoteEntityModel
import com.example.core.remotedatasources.tripremotedatasource.domain.models.DayOfTripRemoteEntityModel
import com.example.core.remotedatasources.tripremotedatasource.domain.models.PlaceRemoteEntityModel
import com.example.core.remotedatasources.tripremotedatasource.domain.models.TripIdentifierRemoteEntityModel
import com.example.core.remotedatasources.tripremotedatasource.domain.models.TripRemoteEntityModel
import com.example.core.databse.dao.models.AddressLocalEntityModel
import com.example.core.databse.dao.models.CoordinatesLocalEntityModel
import com.example.core.databse.dao.models.DayOfTripLocalEntityModel
import com.example.core.databse.dao.models.PlaceLocalEntityModel
import com.example.core.databse.dao.models.TripLocalEntityModel
import com.example.features.savetrip.domain.models.AddressSaveTripDomainModel
import com.example.features.savetrip.domain.models.ContributorSaveTripDomainModel
import com.example.features.savetrip.domain.models.CoordinatesSaveTripDomainModel
import com.example.features.savetrip.domain.models.DayOfTripInfoSaveTripDomainModel
import com.example.features.savetrip.domain.models.DayOfTripSaveTripDomainModel
import com.example.features.savetrip.domain.models.PlaceSaveTripDomainModel
import com.example.features.savetrip.domain.models.SaveTripDataSaveTripDomainModel
import com.example.features.savetrip.domain.models.SaveTripInfoSaveTripDomainModel
import com.example.features.savetrip.domain.models.SaveTripStateSaveTripDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

fun StateFlow<SaveTripStateSaveTripDomainModel>.toFlowOfSaveTripInfo(): Flow<SaveTripInfoSaveTripDomainModel> {

    return this.map { saveTripState ->
        SaveTripInfoSaveTripDomainModel(
            note = saveTripState.saveTripData.note,
            title = saveTripState.saveTripData.title,
            date = saveTripState.saveTripData.date,
            contributorUsernames = saveTripState.saveTripData.selectedContributorsParameters.values.map { it.username },
            daysOfTrip = saveTripState.saveTripData.daysOfTrip.mapIndexed { index, place ->
                place.toDayOfTripInfoDomainModel(index == saveTripState.saveTripOptions.selectedDayPosition)
            }

        )
    }
}

fun StateFlow<SaveTripStateSaveTripDomainModel>.toFlowOfPlacesOfSelectedDay(): Flow<List<PlaceSaveTripDomainModel>> {

    return this.map {

        when(it.saveTripOptions.selectedDayPosition) {
            -1 -> return@map emptyList()
            else -> it.saveTripData.daysOfTrip[it.saveTripOptions.selectedDayPosition].places
        }
    }
}

fun StateFlow<SaveTripStateSaveTripDomainModel>.toFlowOfSelectedContributorsInfoSaveTrip(): Flow<List<ContributorSaveTripDomainModel>> {

    return this.map {
        it.saveTripData.selectedContributorsParameters.values.toList()
    }
}

fun StateFlow<List<ContributorSaveTripDomainModel>>.toFlowOfContributorsInfoSaveTrip(): Flow<List<ContributorSaveTripDomainModel>> {

    return this.map {
        it
    }
}

fun DayOfTripSaveTripDomainModel.toDayOfTripInfoDomainModel(selected: Boolean): DayOfTripInfoSaveTripDomainModel {

    return DayOfTripInfoSaveTripDomainModel(
        label = this.label,
        selected = selected
    )
}

fun SaveTripDataSaveTripDomainModel.toRemoteTripEntity(): TripRemoteEntityModel {
    return TripRemoteEntityModel(
        uUID = this.uUID,
        startPlace = this.startPlace!!.toPlaceRemoteEntityModel(),
        days = this.daysOfTrip.map {
            it.toDayOfTripRemoteEntityModel()
        },
        date = this.date,
        note = this.note,
        title = this.title!!
    )
}

fun PlaceSaveTripDomainModel.toPlaceRemoteEntityModel(): PlaceRemoteEntityModel {
    return PlaceRemoteEntityModel(
        uUID = this.uUID,
        name = this.name,
        cuisine = this.cuisine,
        openingHours = this.openingHours,
        charge = this.charge,
        address = this.address.toAddressRemoteEntityModel(),
        coordinates = this.coordinates.toCoordinatesRemoteEntityModel(),
        category = this.category
    )
}

fun AddressSaveTripDomainModel.toAddressRemoteEntityModel(): AddressRemoteEntityModel {
    return AddressRemoteEntityModel(
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        country = this.country
    )
}

fun CoordinatesSaveTripDomainModel.toCoordinatesRemoteEntityModel(): CoordinatesRemoteEntityModel {
    return CoordinatesRemoteEntityModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun DayOfTripSaveTripDomainModel.toDayOfTripRemoteEntityModel(): DayOfTripRemoteEntityModel {
    return DayOfTripRemoteEntityModel(
        label = this.label,
        places = this.places.map {
            it.toPlaceRemoteEntityModel()
        }
    )
}

fun SaveTripDataSaveTripDomainModel.toTripIdentifierRemoteEntityModel(creatorID: String?): TripIdentifierRemoteEntityModel{
    return TripIdentifierRemoteEntityModel(
        uuid = this.uUID,
        title = this.title,
        contributorUIDs = this.selectedContributorsUIDs,
        contributors = this.selectedContributorsParameters.mapValues {
            it.value.toContributorRemoteEntityModel()
        },
        creatorUID = this.creatorUID?: creatorID
    )
}

fun ContributorSaveTripDomainModel.toContributorRemoteEntityModel(): ContributorRemoteEntityModel {
    return ContributorRemoteEntityModel(
        canUpdate = this.canUpdate
    )
}

fun SaveTripDataSaveTripDomainModel.toLocalTripEntity(): TripLocalEntityModel {
    return TripLocalEntityModel(
        uUID = this.uUID,
        startPlace = this.startPlace!!.toLocalPlaceEntityModel(""),
        date = this.date,
        title = this.title!!,
        note = this.note,
    )
}

fun DayOfTripSaveTripDomainModel.toLocalDayOfTripEntityModel(
    dayUUID: String,
    tripId: String
): DayOfTripLocalEntityModel {
    return DayOfTripLocalEntityModel(
        dayUUID = dayUUID,
        tripId = tripId,
        label = this.label
    )
}

fun PlaceSaveTripDomainModel.toLocalPlaceEntityModel(dayId: String): PlaceLocalEntityModel {
    return PlaceLocalEntityModel(
        uUID = this.uUID,
        dayId = dayId,
        name = this.name,
        cuisine = this.cuisine,
        openingHours = this.openingHours,
        charge = this.charge,
        address = this.address.toAddressLocalEntityModel(),
        coordinates = this.coordinates.toCoordinatesLocalEntityModel(),
        category = this.category
    )
}

fun CoordinatesSaveTripDomainModel.toCoordinatesLocalEntityModel(): CoordinatesLocalEntityModel {
    return CoordinatesLocalEntityModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}
fun AddressSaveTripDomainModel.toAddressLocalEntityModel(): AddressLocalEntityModel {
    return AddressLocalEntityModel(
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        country = this.country
    )
}

fun StateFlow<List<PlaceSaveTripDomainModel>>.toFlowOfAssignablePlaces(): Flow<List<PlaceSaveTripDomainModel>> {

    return this.map {
        it
    }
}