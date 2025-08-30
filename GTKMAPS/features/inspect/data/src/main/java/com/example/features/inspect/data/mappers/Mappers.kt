package com.example.features.inspect.data.mappers

import android.util.Log
import com.example.features.inspect.domain.models.info.DayOfTripInfoInspectTripDomainModel
import com.example.features.inspect.domain.models.DayOfTripInspectTripDomainModel
import com.example.features.inspect.domain.models.map.DayOfTripMapDataInspectTripDomainModel
import com.example.features.inspect.domain.models.map.InspectTripMapDataInspectTripDomainModel
import com.example.features.inspect.domain.models.InspectTripStateInspectTripDomainModel
import com.example.features.inspect.domain.models.PlaceDetailsInspectTripDomainModel
import com.example.features.inspect.domain.models.info.InspectTripTripInfoInspectTripDomainModel
import com.example.features.inspect.domain.models.info.PlaceInfoInspectTripDomainModel
import com.example.features.inspect.domain.models.PlaceInspectTripDomainModel
import com.example.features.inspect.domain.models.map.PlaceMapDataInspectTripDomainModel
import com.example.features.inspect.domain.models.TripInspectTripDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

fun StateFlow<InspectTripStateInspectTripDomainModel>.toFlowOfInspectedTripInfo(): Flow<InspectTripTripInfoInspectTripDomainModel> {
    return this.map { inspectedTrip ->

        when(inspectedTrip.inspectedTrip) {
            is TripInspectTripDomainModel.Inspected -> InspectTripTripInfoInspectTripDomainModel.Inspected(
                uUID = (inspectedTrip.inspectedTrip as TripInspectTripDomainModel.Inspected).uUID,
                startInfo = (inspectedTrip.inspectedTrip as TripInspectTripDomainModel.Inspected).startPlace.toInfoOfPlaceInspectTripModel(),
                creatorUsername = (inspectedTrip.inspectedTrip as TripInspectTripDomainModel.Inspected).creatorUsername,
                title = (inspectedTrip.inspectedTrip as TripInspectTripDomainModel.Inspected).title,
                daysInfo = (inspectedTrip.inspectedTrip as TripInspectTripDomainModel.Inspected).days.mapIndexed { index, day ->
                    day.toInfoOfDayOfTripInspectTripModel(index == inspectedTrip.inspectTripOptions.currentSelectedDayPosition)
                }
            )
            is TripInspectTripDomainModel.Default -> InspectTripTripInfoInspectTripDomainModel.Default
        }

    }
}

fun PlaceInspectTripDomainModel.toInfoOfPlaceInspectTripModel(): PlaceInfoInspectTripDomainModel {

    return PlaceInfoInspectTripDomainModel(
        name = this.getPlaceInfo(),
        addressInfo = this.getPlaceAddressInfo()
    )
}

fun DayOfTripInspectTripDomainModel.toInfoOfDayOfTripInspectTripModel(selected: Boolean): DayOfTripInfoInspectTripDomainModel {

    return DayOfTripInfoInspectTripDomainModel(
        selected = selected,
        label = this.label,
        placesInfo = this.places.map {it.toInfoOfPlaceInspectTripModel()}
    )
}

fun StateFlow<InspectTripStateInspectTripDomainModel>.toFlowOfInspectTripMapData(): Flow<InspectTripMapDataInspectTripDomainModel> {

    return this.map { inspectedTrip ->

        when(inspectedTrip.inspectedTrip) {
            is TripInspectTripDomainModel.Inspected -> {

                Log.d("inspectedTrip", "Number of places for day #${inspectedTrip.inspectTripOptions.currentSelectedDayPosition} \n" +
                        " ${(inspectedTrip.inspectedTrip as TripInspectTripDomainModel.Inspected).days[inspectedTrip.inspectTripOptions.currentSelectedDayPosition].places.size}")

                InspectTripMapDataInspectTripDomainModel.Inspected(
                    uuid = (inspectedTrip.inspectedTrip as TripInspectTripDomainModel.Inspected).uUID,
                    startMapData = (inspectedTrip.inspectedTrip as TripInspectTripDomainModel.Inspected).startPlace.toPlaceMapDataInspectTrip(),
                    daysMapData = (inspectedTrip.inspectedTrip as TripInspectTripDomainModel.Inspected).days.filterIndexed { index, day -> index == inspectedTrip.inspectTripOptions.currentSelectedDayPosition }
                        .map { it.toDayOfTripMapData() }
                )
            }
            is TripInspectTripDomainModel.Default -> InspectTripMapDataInspectTripDomainModel.Default
        }
    }
}

fun PlaceInspectTripDomainModel.toPlaceMapDataInspectTrip(): PlaceMapDataInspectTripDomainModel {

    return PlaceMapDataInspectTripDomainModel(
        uuid = this.uUID,
        placeName = this.name,
        coordinates = this.coordinates,
        category = this.category
    )
}

fun DayOfTripInspectTripDomainModel.toDayOfTripMapData(): DayOfTripMapDataInspectTripDomainModel {

    return DayOfTripMapDataInspectTripDomainModel(
        placesMapData = this.places.map{ it.toPlaceMapDataInspectTrip()}
    )
}

fun PlaceInspectTripDomainModel.toPlaceDetailsDomainModel(): PlaceDetailsInspectTripDomainModel {

    return PlaceDetailsInspectTripDomainModel(
        uuid = this.uUID,
        name = this.name,
        addressInfo = this.address.getAddressInfo(),
        cuisine = this.cuisine,
        openingHours = this.openingHours,
        charge = this.charge
    )
}