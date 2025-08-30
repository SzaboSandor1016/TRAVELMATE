package com.example.features.route.presentation.mappers

import com.example.features.inspect.domain.models.AddressInspectTripDomainModel
import com.example.features.inspect.domain.models.CoordinatesInspectTripDomainModel
import com.example.features.inspect.domain.models.PlaceInspectTripDomainModel
import com.example.features.route.domain.models.CoordinatesRouteDomainModel
import com.example.features.route.domain.models.info.RouteInfoNodeRouteDomainModel
import com.example.features.route.domain.models.info.RouteInfoRouteDomainModel
import com.example.features.route.presentation.models.AddressRoutePresentationModel
import com.example.features.route.presentation.models.CoordinatesRoutePresentationModel
import com.example.features.route.presentation.models.PlaceRoutePresentationModel
import com.example.features.route.presentation.models.RouteInfoNodeRoutePresentationModel
import com.example.features.route.presentation.models.RouteInfoRoutePresentationModel
import com.example.features.search.domain.models.searchmodels.AddressSearchDomainModel
import com.example.features.search.domain.models.searchmodels.CoordinatesSearchDomainModel
import com.example.features.search.domain.models.searchmodels.PlaceSearchDomainModel
import com.example.features.selectedplace.domain.models.AddressSelectedPlaceDomainModel
import com.example.features.selectedplace.domain.models.CoordinatesSelectedPlaceDomainModel
import com.example.features.selectedplace.domain.models.SelectedPlaceSelectedPlaceDomainModel

fun RouteInfoRouteDomainModel.toRouteInfoPresentationModel(): RouteInfoRoutePresentationModel {
    return RouteInfoRoutePresentationModel(
        infoNodes = this.infoNodes.map { it.toRouteInfoNodePresentationModel() },
        transportMode = this.transportMode,
        fullWalkDuration = this.fullWalkDuration,
        fullCarDuration = this.fullCarDuration,
        fullWalkDistance = this.fullWalkDistance,
        fullCarDistance = this.fullCarDistance
    )
}
fun RouteInfoNodeRouteDomainModel.toRouteInfoNodePresentationModel(): RouteInfoNodeRoutePresentationModel {
    return RouteInfoNodeRoutePresentationModel(
        placeUUID = this.placeUUID,
        name = this.name,
        coordinates = this.coordinates.toCoordinatesRoutePresentationModel(),
        duration = this.duration,
        distance = this.distance,
    )
}

fun CoordinatesRouteDomainModel.toCoordinatesRoutePresentationModel(): CoordinatesRoutePresentationModel {

    return CoordinatesRoutePresentationModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun PlaceSearchDomainModel.toPlaceRoutePresentationModel(): PlaceRoutePresentationModel {

    return PlaceRoutePresentationModel(
        uUID = this.getUUID(),
        name = this.getName(),
        cuisine = this.getCuisine(),
        openingHours = this.getOpeningHours(),
        charge = this.getCharge(),
        address = this.getAddress().toAddressRoutePresentationModel(),
        coordinates = this.getCoordinates().toCoordinatesRoutePresentationModel(),
        category = this.getCategory()
    )
}

fun AddressSearchDomainModel.toAddressRoutePresentationModel(): AddressRoutePresentationModel {

    return AddressRoutePresentationModel(
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        country = this.country
    )
}

fun CoordinatesSearchDomainModel.toCoordinatesRoutePresentationModel(): CoordinatesRoutePresentationModel {

    return CoordinatesRoutePresentationModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun PlaceInspectTripDomainModel.toPlaceRoutePresentationModel(): PlaceRoutePresentationModel {

    return PlaceRoutePresentationModel(
        uUID = this.uUID,
        name = this.name,
        cuisine = this.cuisine,
        openingHours = this.openingHours,
        charge = this.charge,
        address = this.address.toAddressRoutePresentationModel(),
        coordinates = this.coordinates.toCoordinatesRoutePresentationModel(),
        category = this.category
    )
}

fun AddressInspectTripDomainModel.toAddressRoutePresentationModel(): AddressRoutePresentationModel {

    return AddressRoutePresentationModel(
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        country = this.country
    )
}

fun CoordinatesInspectTripDomainModel.toCoordinatesRoutePresentationModel(): CoordinatesRoutePresentationModel {

    return CoordinatesRoutePresentationModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun PlaceRoutePresentationModel.toPlaceSelectedPlaceDomainModel(): SelectedPlaceSelectedPlaceDomainModel.Selected {

    return SelectedPlaceSelectedPlaceDomainModel.Selected(
        uUID = this.uUID,
        name = this.name,
        cuisine = this.cuisine,
        openingHours = this.openingHours,
        charge = this.charge,
        address = this.address.toAddressRoutePresentationModel(),
        coordinates = this.coordinates.toCoordinatesRoutePresentationModel(),
        category = this.category
    )
}

fun AddressRoutePresentationModel.toAddressRoutePresentationModel(): AddressSelectedPlaceDomainModel {

    return AddressSelectedPlaceDomainModel(
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        country = this.country
    )
}

fun CoordinatesRoutePresentationModel.toCoordinatesRoutePresentationModel(): CoordinatesSelectedPlaceDomainModel {

    return CoordinatesSelectedPlaceDomainModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}