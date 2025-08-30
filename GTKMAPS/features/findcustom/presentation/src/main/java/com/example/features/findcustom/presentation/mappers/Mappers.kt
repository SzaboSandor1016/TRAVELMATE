package com.example.features.findcustom.presentation.mappers

import com.example.features.findcustom.domain.models.AddressCustomPlaceDomainModel
import com.example.features.findcustom.domain.models.CoordinatesCustomPlaceDomainModel
import com.example.features.findcustom.domain.models.CustomPlaceInfoCustomPlaceDomainModel
import com.example.features.findcustom.domain.models.PlaceCustomPlaceDomainModel
import com.example.features.search.domain.models.searchmodels.AddressSearchDomainModel
import com.example.features.search.domain.models.searchmodels.CoordinatesSearchDomainModel
import com.example.features.search.domain.models.searchmodels.PlaceSearchDomainModel
import com.example.features.findcustom.presentation.models.AddressCustomPlacePresentationModel
import com.example.features.findcustom.presentation.models.CoordinatesCustomPlacePresentationModel
import com.example.features.findcustom.presentation.models.CustomPlaceInfoPresentationModel
import com.example.features.findcustom.presentation.models.PlaceCustomPlacePresentationModel


fun CustomPlaceInfoCustomPlaceDomainModel.CustomPlace.toCustomPlaceUiModel(): CustomPlaceInfoPresentationModel {

    return CustomPlaceInfoPresentationModel(
        name = this.name!!,
        address = AddressCustomPlacePresentationModel(
            this.address.city,
            this.address.street,
            this.address.houseNumber,
            this.address.country
        )
    )
}

fun PlaceCustomPlaceDomainModel.CustomPlace.toCustomPlacePresentationModel(): PlaceCustomPlacePresentationModel {

    return PlaceCustomPlacePresentationModel(
        uUID = this.uUID,
        name = this.name,
        address = this.address.toAddressSearchDomainModel(),
        coordinates = this.coordinates.toCoordinatesSearchDomainModel()
    )
}

fun AddressCustomPlaceDomainModel.toAddressSearchDomainModel(): AddressCustomPlacePresentationModel {

    return AddressCustomPlacePresentationModel(
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        country = this.country
    )
}

fun CoordinatesCustomPlaceDomainModel.toCoordinatesSearchDomainModel(): CoordinatesCustomPlacePresentationModel {
    return CoordinatesCustomPlacePresentationModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun PlaceCustomPlacePresentationModel.toPlaceSearchDomainModel(): PlaceSearchDomainModel {

    return PlaceSearchDomainModel(
        uUID = this.uUID!!,
        name = this.name,
        address = this.address.toAddressSearchDomainModel(),
        coordinates = this.coordinates.toCoordinatesSearchDomainModel(),
        cuisine = null,
        openingHours = null,
        charge = null,
        category = "start"
    )
}

fun AddressCustomPlacePresentationModel.toAddressSearchDomainModel(): AddressSearchDomainModel {

    return AddressSearchDomainModel(
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        country = this.country
    )
}

fun CoordinatesCustomPlacePresentationModel.toCoordinatesSearchDomainModel(): CoordinatesSearchDomainModel {
    return CoordinatesSearchDomainModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}