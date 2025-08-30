package com.example.features.selectedplace.presentation.mappers

import com.example.features.route.domain.models.AddressRouteDomainModel
import com.example.features.route.domain.models.CoordinatesRouteDomainModel
import com.example.features.route.domain.models.PlaceRouteDomainModel
import com.example.features.savetrip.domain.models.AddressSaveTripDomainModel
import com.example.features.savetrip.domain.models.CoordinatesSaveTripDomainModel
import com.example.features.savetrip.domain.models.PlaceSaveTripDomainModel
import com.example.features.selectedplace.domain.models.AddressSelectedPlaceDomainModel
import com.example.features.selectedplace.domain.models.CoordinatesSelectedPlaceDomainModel
import com.example.features.selectedplace.domain.models.SelectedPlaceInfoSelectedPlaceDomainModel
import com.example.features.selectedplace.domain.models.SelectedPlaceSelectedPlaceDomainModel
import com.example.features.selectedplace.presentation.models.AddressSelectedPlacePresentationModel
import com.example.features.selectedplace.presentation.models.SelectedPlaceOptionsPresentationModel
import com.example.features.selectedplace.presentation.models.SelectedPlaceSelectedPlacePresentationModel
import com.example.features.selectedplaceoptions.domain.models.SelectedPlaceSelectedPlaceOptions

fun SelectedPlaceInfoSelectedPlaceDomainModel.Selected.toSelectedPlacePresentationModel(): SelectedPlaceSelectedPlacePresentationModel.Selected {

    return SelectedPlaceSelectedPlacePresentationModel.Selected(
        uuid = this.uUID,
        name = this.name,
        cuisine = this.cuisine,
        openingHours = this.openingHours,
        charge = this.charge,
        address = this.address.toAddressSelectedPlacePresentationModel(),
    )
}

fun AddressSelectedPlaceDomainModel.toAddressSelectedPlacePresentationModel(): AddressSelectedPlacePresentationModel {

    return AddressSelectedPlacePresentationModel(
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        country = this.country
    )
}

fun SelectedPlaceSelectedPlaceDomainModel.Selected.toPlaceSaveTripDomainModel(): PlaceSaveTripDomainModel {

    return PlaceSaveTripDomainModel(
        dayOfTripUUID = null,
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

fun AddressSelectedPlaceDomainModel.toAddressSaveTripDomainModel(): AddressSaveTripDomainModel {

    return AddressSaveTripDomainModel(
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        country = this.country
    )
}

fun CoordinatesSelectedPlaceDomainModel.toCoordinatesSaveTripDomainModel(): CoordinatesSaveTripDomainModel {

    return CoordinatesSaveTripDomainModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun SelectedPlaceSelectedPlaceDomainModel.Selected.toPlaceRouteDomainModel(): PlaceRouteDomainModel {

    return PlaceRouteDomainModel(
        uUID = this.uUID,
        name = this.name,
        coordinates = this.coordinates.toCoordinatesRouteDomainModel(),
    )
}

fun AddressSelectedPlaceDomainModel.toAddressRouteDomainModel(): AddressRouteDomainModel {

    return AddressRouteDomainModel(
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        country = this.country
    )
}

fun CoordinatesSelectedPlaceDomainModel.toCoordinatesRouteDomainModel(): CoordinatesRouteDomainModel {

    return CoordinatesRouteDomainModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun SelectedPlaceSelectedPlaceOptions.toSelectedPlaceOptionsPresentationModel(): SelectedPlaceOptionsPresentationModel {
    
    return SelectedPlaceOptionsPresentationModel(
        origin = this.origin,
        containerState = this.containerState
    )
}