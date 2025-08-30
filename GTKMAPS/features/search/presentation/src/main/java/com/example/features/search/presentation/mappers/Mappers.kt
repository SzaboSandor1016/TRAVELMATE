package com.example.features.search.presentation.mappers

import com.example.features.route.domain.models.AddressRouteDomainModel
import com.example.features.route.domain.models.CoordinatesRouteDomainModel
import com.example.features.route.domain.models.PlaceRouteDomainModel
import com.example.features.savetrip.domain.models.AddressSaveTripDomainModel
import com.example.features.savetrip.domain.models.CoordinatesSaveTripDomainModel
import com.example.features.savetrip.domain.models.PlaceSaveTripDomainModel
import com.example.features.search.domain.models.searchmodels.AddressSearchDomainModel
import com.example.features.search.domain.models.searchmodels.CoordinatesSearchDomainModel
import com.example.features.search.domain.models.searchmodels.PlaceSearchDomainModel
import com.example.features.search.domain.models.searchmodels.SearchStartInfoSearchDomainModel
import com.example.features.search.domain.models.searchoptionsmodels.SearchOptionsInfoSearchDomainModel
import com.example.features.search.presentation.models.AddressSearchPresentationModel
import com.example.features.search.presentation.models.CoordinatesSearchPresentationModel
import com.example.features.selectedplace.domain.models.AddressSelectedPlaceDomainModel
import com.example.features.selectedplace.domain.models.CoordinatesSelectedPlaceDomainModel
import com.example.features.selectedplace.domain.models.SelectedPlaceSelectedPlaceDomainModel
import com.example.features.search.presentation.models.PlaceSearchPresentationModel
import com.example.features.search.presentation.models.SearchOptionsInfoSearchPresentationModel
import com.example.features.search.presentation.models.SearchStartSearchPresentationModel

fun SearchStartInfoSearchDomainModel.toSearchInfoPresentationModel(): SearchStartSearchPresentationModel {
    
    return SearchStartSearchPresentationModel(
        name = this.name,
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        country = this.country
    )
}

fun SearchOptionsInfoSearchDomainModel.toSearchOptionsPresentationModel(): SearchOptionsInfoSearchPresentationModel {
    return SearchOptionsInfoSearchPresentationModel(
        parametersSelected = this.parametersSelected,
        transportSelected = this.transportSelected
    )
}

fun PlaceSearchDomainModel.toPlaceSearchPresentationModel(): PlaceSearchPresentationModel {

    return PlaceSearchPresentationModel(
        uuid = this.getUUID(),
        name = this.getName(),
        address = this.getAddress().toAddressSearchPresentationModel(),
        coordinates = this.getCoordinates().toCoordinatesSearchDomainModel(),
        category = this.getCategory()
    )
}

fun PlaceSearchDomainModel.toPlaceSaveTripDomainModel(dayOfTripUUID: String? = null): PlaceSaveTripDomainModel {

    return PlaceSaveTripDomainModel(
        dayOfTripUUID = dayOfTripUUID,
        uUID = this.getUUID(),
        name = this.getName(),
        cuisine = this.getCuisine(),
        openingHours = this.getOpeningHours(),
        charge = this.getCharge(),
        address = this.getAddress().toAddressSaveTripDomainModel(),
        coordinates = this.getCoordinates().toCoordinatesSaveTripModel(),
        category = this.getCategory()
    )
}

fun AddressSearchDomainModel.toAddressSearchPresentationModel(): AddressSearchPresentationModel {

    return AddressSearchPresentationModel(
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        country = this.country
    )
}

fun CoordinatesSearchDomainModel.toCoordinatesSearchDomainModel(): CoordinatesSearchPresentationModel {

    return CoordinatesSearchPresentationModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun AddressSearchDomainModel.toAddressSaveTripDomainModel(): AddressSaveTripDomainModel {

    return AddressSaveTripDomainModel(
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        country = this.country
    )
}

fun CoordinatesSearchDomainModel.toCoordinatesSaveTripModel(): CoordinatesSaveTripDomainModel {

    return CoordinatesSaveTripDomainModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun PlaceSearchPresentationModel.toPlaceSearchDomainModel(): PlaceSearchDomainModel {

    return PlaceSearchDomainModel(
        uUID = this.uuid,
        name = this.name,
        cuisine = null,
        openingHours = null,
        charge = null,
        address = this.address.toAddressSearchDomainModel(),
        coordinates = this.coordinates.toCoordinatesSearchDomainModel(),
        category = this.category
    )
}

fun AddressSearchPresentationModel.toAddressSearchDomainModel(): AddressSearchDomainModel {

    return AddressSearchDomainModel(
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        country = this.country
    )
}

fun CoordinatesSearchPresentationModel.toCoordinatesSearchDomainModel(): CoordinatesSearchDomainModel {

    return CoordinatesSearchDomainModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun PlaceSearchPresentationModel.toPlaceSelectedPlaceDomainModel(): SelectedPlaceSelectedPlaceDomainModel.Selected {

    return SelectedPlaceSelectedPlaceDomainModel.Selected(
        uUID = this.uuid,
        name = this.name,
        cuisine = null,
        openingHours = null,
        charge = null,
        address = this.address.toAddressSelectedPlaceDomainModel(),
        coordinates = this.coordinates.toCoordinatesSelectedPlaceDomainModel(),
        category = this.category
    )
}

fun AddressSearchPresentationModel.toAddressSelectedPlaceDomainModel(): AddressSelectedPlaceDomainModel {

    return AddressSelectedPlaceDomainModel(
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        country = this.country
    )
}

fun CoordinatesSearchPresentationModel.toCoordinatesSelectedPlaceDomainModel(): CoordinatesSelectedPlaceDomainModel {

    return CoordinatesSelectedPlaceDomainModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun AddressSearchPresentationModel.toAddressRouteDomainModel(): AddressRouteDomainModel {

    return AddressRouteDomainModel(
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        country = this.country
    )
}

fun CoordinatesSearchPresentationModel.toCoordinatesRouteDomainModel(): CoordinatesRouteDomainModel {

    return CoordinatesRouteDomainModel(
        latitude = latitude,
        longitude = longitude
    )
}