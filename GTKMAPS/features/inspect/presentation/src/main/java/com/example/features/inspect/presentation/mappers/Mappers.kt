package com.example.features.inspect.presentation.mappers

import com.example.features.inspect.domain.models.info.DayOfTripInfoInspectTripDomainModel
import com.example.features.inspect.domain.models.info.InspectTripTripInfoInspectTripDomainModel
import com.example.features.inspect.domain.models.info.PlaceInfoInspectTripDomainModel
import com.example.features.inspect.presentation.models.DayOfTripInfoInspectTripPresentationModel
import com.example.features.inspect.presentation.models.InspectTripInfoUIModel
import com.example.features.inspect.presentation.models.PlaceInfoInspectTripPresentationModel

fun InspectTripTripInfoInspectTripDomainModel.mapToInspectTripInfoPresentationModel(): InspectTripInfoUIModel {
    
    return when(this) {
        is InspectTripTripInfoInspectTripDomainModel.Inspected -> InspectTripInfoUIModel.Inspected(
            uuid = this.uUID,
            title = this.title,
            startInfo = this.startInfo.toPlaceInfoPresentationModel(),
            daysInfo = this.daysInfo.map{ it.toDayOfTripInfoPresentationModel()},
            creatorUsername = this.creatorUsername
        )
        is InspectTripTripInfoInspectTripDomainModel.Default -> InspectTripInfoUIModel.Default
    }
}

fun DayOfTripInfoInspectTripDomainModel.toDayOfTripInfoPresentationModel(): DayOfTripInfoInspectTripPresentationModel {

    return DayOfTripInfoInspectTripPresentationModel(
        selected = this.selected,
        label = this.label,
        placesInfo = this.placesInfo.map { it.toPlaceInfoPresentationModel() }
    )
}

fun PlaceInfoInspectTripDomainModel.toPlaceInfoPresentationModel() :PlaceInfoInspectTripPresentationModel {
    return PlaceInfoInspectTripPresentationModel(
        name = this.name,
        addressInfo = this.addressInfo
    )
}