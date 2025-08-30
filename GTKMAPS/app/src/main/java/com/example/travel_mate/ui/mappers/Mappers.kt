package com.example.travel_mate.ui.mappers

import com.example.features.inspect.domain.models.AddressInspectTripDomainModel
import com.example.features.findcustom.domain.models.CoordinatesCustomPlaceDomainModel
import com.example.features.inspect.domain.models.CoordinatesInspectTripDomainModel
import com.example.features.navigation.domain.models.CoordinatesNavigationDomainModel
import com.example.features.findcustom.domain.models.CustomPlaceMapDataCustomPlaceDomainModel
import com.example.features.navigation.domain.models.NavigationMapDataNavigationDomainModel
import com.example.features.inspect.domain.models.PlaceInspectTripDomainModel
import com.example.features.navigation.domain.models.RouteStepNavigationDomainModel
import com.example.features.inspect.domain.models.map.DayOfTripMapDataInspectTripDomainModel
import com.example.features.inspect.domain.models.map.InspectTripMapDataInspectTripDomainModel
import com.example.features.inspect.domain.models.map.PlaceMapDataInspectTripDomainModel
import com.example.features.route.domain.models.CoordinatesRouteDomainModel
import com.example.features.route.domain.models.PlaceRouteDomainModel
import com.example.features.search.domain.models.searchmodels.AddressSearchDomainModel
import com.example.features.search.domain.models.searchmodels.CoordinatesSearchDomainModel
import com.example.features.search.domain.models.searchmodels.PlaceDataSearchDomainModel
import com.example.features.search.domain.models.searchmodels.PlaceSearchDomainModel
import com.example.features.search.presentation.mappers.toCoordinatesRouteDomainModel
import com.example.features.search.presentation.models.PlaceSearchPresentationModel
import com.example.travel_mate.ui.models.AddressMapPresentationModel
import com.example.travel_mate.ui.models.CoordinatesMapPresentationModel
import com.example.travel_mate.ui.models.DayOfTripMapPresentationModel
import com.example.travel_mate.ui.models.MapDataMapPresentationModel
import com.example.travel_mate.ui.models.PlaceDataMapPresentationModel
import com.example.travel_mate.ui.models.PlaceFullMapPresentationModel
import com.example.travel_mate.ui.models.RouteStepMapPresentationModel
import com.example.features.selectedplace.domain.models.AddressSelectedPlaceDomainModel
import com.example.features.selectedplace.domain.models.CoordinatesSelectedPlaceDomainModel
import com.example.features.selectedplace.domain.models.SelectedPlaceSelectedPlaceDomainModel
import com.example.features.selectedplaceoptions.domain.models.MainSelectedPlaceOptions
import com.example.travel_mate.ui.models.SelectedPlaceOptionsMainPresentationModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine


inline fun <T1,T2,T3,T4,T5,T6,T7,T8,R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    crossinline transform: suspend (T1,T2,T3,T4,T5,T6,T7,T8) -> R
): Flow<R> = combine(flow, flow2, flow3, flow4, flow5, flow6, flow7, flow8) { args: Array<*> ->

    @Suppress("UNCHECKED_CAST")
    transform(
        args[0] as T1,
        args[1] as T2,
        args[2] as T3,
        args[3] as T4,
        args[4] as T5,
        args[5] as T6,
        args[6] as T7,
        args[7] as T8
    )
}

fun PlaceSearchDomainModel.toPlaceFullMapPresentationModel(): PlaceFullMapPresentationModel {

    return PlaceFullMapPresentationModel(
        uUID = this.getUUID(),
        name = this.getName(),
        cuisine = this.getCuisine(),
        openingHours = this.getOpeningHours(),
        charge = this.getCharge(),
        address = this.getAddress().toAddressMapPresentationModel(),
        coordinates = this.getCoordinates().toCoordinatesMapPresentationModel(),
        category = this.getCategory()
    )
}

fun AddressSearchDomainModel.toAddressMapPresentationModel(): AddressMapPresentationModel {

    return AddressMapPresentationModel(
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        country = this.country
    )
}

fun CoordinatesSearchDomainModel.toCoordinatesMapPresentationModel(): CoordinatesMapPresentationModel {

    return CoordinatesMapPresentationModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun PlaceFullMapPresentationModel.toPlaceSelectedPlaceDomainModel(): SelectedPlaceSelectedPlaceDomainModel.Selected {

    return SelectedPlaceSelectedPlaceDomainModel.Selected(
        uUID = this.uUID,
        name = this.name,
        cuisine = this.cuisine,
        openingHours = this.openingHours,
        charge = this.charge,
        address = this.address.toAddressSelectedPlaceDomainModel(),
        coordinates = this.coordinates.toCoordinatesSelectedPlaceDomainModel(),
        category = this.category
    )
}

fun AddressMapPresentationModel.toAddressSelectedPlaceDomainModel(): AddressSelectedPlaceDomainModel {

    return AddressSelectedPlaceDomainModel(
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        country = this.country
    )
}

fun CoordinatesMapPresentationModel.toCoordinatesSelectedPlaceDomainModel(): CoordinatesSelectedPlaceDomainModel {

    return CoordinatesSelectedPlaceDomainModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun MainSelectedPlaceOptions.toSelectedPlaceOptionsPresentationModel(): SelectedPlaceOptionsMainPresentationModel {
    
    return SelectedPlaceOptionsMainPresentationModel(
        containerHeight = this.containerHeight
    )
}

//-------------------------------------------------------------------
//MAP DATA FLOWS MAPPINGS
//-------------------------------------------------------------------
//START
//--------------------------------------------------------------------

//NAVIGATION
fun NavigationMapDataNavigationDomainModel.NavigationMapData.toMapDataMapPresentationModel(): MapDataMapPresentationModel.Navigation {

    return MapDataMapPresentationModel.Navigation(
        goal = this.goal.toCoordinatesMapDataPresentationModel()!!,
        route = this.route
    )
}

fun CoordinatesNavigationDomainModel?.toCoordinatesMapDataPresentationModel(): CoordinatesMapPresentationModel? {

    if(this == null) return null

    return CoordinatesMapPresentationModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun RouteStepNavigationDomainModel.toRouteStepMapDataPresentationModel(): RouteStepMapPresentationModel {

    return RouteStepMapPresentationModel(
        instruction = this.instruction,
        name = this.name,
        distance = this.distance,
        duration = this.duration,
        type = this.type,
        coordinates = this.coordinates.toCoordinatesMapDataPresentationModel()!!
    )
}
//NAVIGATION

//INSPECT TRIP

fun InspectTripMapDataInspectTripDomainModel.Inspected.toMapDataPresentationModel(): MapDataMapPresentationModel.Inspect {

    return MapDataMapPresentationModel.Inspect(
        startPlace = startMapData.toPlaceMapPresentationModel(),
        days = this.daysMapData.map { it.toDayOfTripMapPresentationModel() }
    )
}

fun DayOfTripMapDataInspectTripDomainModel.toDayOfTripMapPresentationModel(): DayOfTripMapPresentationModel {

    return DayOfTripMapPresentationModel(
        places = this.placesMapData.map { it.toPlaceMapPresentationModel()}
    )
}
fun PlaceMapDataInspectTripDomainModel.toPlaceMapPresentationModel(): PlaceDataMapPresentationModel {

    return PlaceDataMapPresentationModel(
        uuid = this.uuid,
        name = this.placeName,
        coordinates = this.coordinates.toCoordinatesMapPresentationModel(),
        category = this.category
    )
}

fun CoordinatesInspectTripDomainModel.toCoordinatesMapPresentationModel(): CoordinatesMapPresentationModel {

    return CoordinatesMapPresentationModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun PlaceInspectTripDomainModel.toPlaceFullMapPresentationModel(): PlaceFullMapPresentationModel {

    return PlaceFullMapPresentationModel(
        uUID = this.uUID,
        name = this.name,
        cuisine = this.cuisine,
        openingHours = this.openingHours,
        charge = this.charge,
        address = this.address.toAddressMapPresentationModel(),
        coordinates = this.coordinates.toCoordinatesMapPresentationModel(),
        category = this.category
    )
}

fun AddressInspectTripDomainModel.toAddressMapPresentationModel(): AddressMapPresentationModel {

    return AddressMapPresentationModel(
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        country = this.country
    )
}

//INSPECT TRIP

//SEARCH
fun PlaceDataSearchDomainModel.toPlaceDataMapPresentationModel(): PlaceDataMapPresentationModel {

    return PlaceDataMapPresentationModel(
        uuid = this.uUID,
        name = this.name,
        coordinates = this.coordinates.toCoordinatesMapPresentationModel(),
        category = this.category
    )
}
//SEARCH

//CUSTOM PLACE
fun CustomPlaceMapDataCustomPlaceDomainModel.CustomPlace.toPlaceMapPresentationModel(): PlaceDataMapPresentationModel {
    
    return PlaceDataMapPresentationModel(
        uuid = this.uuid,
        name = this.name,
        coordinates = this.coordinates.toCoordinatesMapPresentationModel(),
        category = this.category
    )
}

fun CoordinatesCustomPlaceDomainModel.toCoordinatesMapPresentationModel(): CoordinatesMapPresentationModel {

    return CoordinatesMapPresentationModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}
//CUSTOM PLACE

fun PlaceDataMapPresentationModel.toPlaceRouteDomainModel(): PlaceRouteDomainModel {

    return PlaceRouteDomainModel(
        uUID = this.uuid,
        name = this.name,
        coordinates = this.coordinates.toCoordinatesRouteDomainModel(),
    )
}

fun CoordinatesMapPresentationModel.toCoordinatesRouteDomainModel(): CoordinatesRouteDomainModel {

    return CoordinatesRouteDomainModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}