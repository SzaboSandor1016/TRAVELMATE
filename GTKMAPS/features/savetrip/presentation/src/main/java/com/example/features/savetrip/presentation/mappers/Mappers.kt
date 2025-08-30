package com.example.features.savetrip.presentation.mappers

import com.example.features.savetrip.domain.models.ContributorSaveTripDomainModel
import com.example.features.savetrip.domain.models.DayOfTripInfoSaveTripDomainModel
import com.example.features.savetrip.domain.models.DayOfTripSaveTripDomainModel
import com.example.features.savetrip.domain.models.PlaceSaveTripDomainModel
import com.example.features.savetrip.domain.models.SaveTripInfoSaveTripDomainModel
import com.example.features.savetrip.presentation.models.ContributorSaveTripPresentationModel
import com.example.features.savetrip.presentation.models.DayOfTripSaveTripPresentationModel
import com.example.features.savetrip.presentation.models.PlaceSaveTripPresentationModel
import com.example.features.savetrip.presentation.models.SaveTripInfoPresentationModel

fun SaveTripInfoSaveTripDomainModel.toSaveTripInfoPresentationModel(): SaveTripInfoPresentationModel {
    return SaveTripInfoPresentationModel(
        date = this.date,
        title = this.title,
        note = this.note,
        contributorUsernames = this.contributorUsernames,
        daysOfTrip = this.daysOfTrip.map{ it.toDayOfTripPresentationModel()}
    )
}

fun DayOfTripInfoSaveTripDomainModel.toDayOfTripPresentationModel(): DayOfTripSaveTripPresentationModel {
    return DayOfTripSaveTripPresentationModel(
        label = this.label,
        selected = this.selected
        //places = this.places.map { it.toPlaceSaveTripPresentationModel() }
    )
}

fun PlaceSaveTripDomainModel.toPlaceSaveTripPresentationModel(): PlaceSaveTripPresentationModel {
    return PlaceSaveTripPresentationModel(
        uUID = this.uUID,
        name = this.name
    )
}

fun ContributorSaveTripDomainModel.toContributorSaveTripPresentationModel(selected: Boolean): ContributorSaveTripPresentationModel {
    return ContributorSaveTripPresentationModel(
        uid = this.uid,
        username = this.username,
        canUpdate = this.canUpdate,
        selected = selected
    )
}
