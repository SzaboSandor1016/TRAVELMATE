package com.example.features.savetrip.domain.repository

import com.example.features.savetrip.domain.models.ContributorSaveTripDomainModel
import com.example.features.savetrip.domain.models.PlaceSaveTripDomainModel
import com.example.features.savetrip.domain.models.SaveTripDataSaveTripDomainModel
import com.example.features.savetrip.domain.models.SaveTripInfoSaveTripDomainModel
import kotlinx.coroutines.flow.Flow

interface SaveTripRepository {

    fun hasUserSignedIn(): Flow<Boolean>
    fun getSaveTripInfo(): Flow<SaveTripInfoSaveTripDomainModel>

    fun getPlacesOfDayOfTripBy(): Flow<List<PlaceSaveTripDomainModel>>

    fun getSaveTripContributorsInfo(): Flow<List<ContributorSaveTripDomainModel>>

    fun getSelectableContributorsInfo(): Flow<List<ContributorSaveTripDomainModel>>

    fun isContainedInAssignablePlaces(uuid: String): Flow<Boolean>

    fun areAssignablePlacesEmpty(): Flow<Boolean>

    fun getAssignablePlaces(): Flow<List<PlaceSaveTripDomainModel>>

    fun areContributorsEmpty(): Boolean

    suspend fun initSaveFromSearchWithStartPlace(
        startPlace: PlaceSaveTripDomainModel
    )

    suspend fun initSaveBySelectingTripToUpdate(
        tripData: SaveTripDataSaveTripDomainModel
    )

    suspend fun clearAssignablePlaces()

    suspend fun setSelectedDayWith(index: Int)

    suspend fun addRemoveAssignablePlace(
        place: PlaceSaveTripDomainModel
    )

    suspend fun assignPlaceToDayOfTrip(
        placeUUID: String
    )

    suspend fun removePlaceFromDayOfTrip(
        placeUUID: String
    )

    suspend fun setTripTitle(title: String)

    suspend fun setTripDate(date: String)

    suspend fun setTripNote(note: String)

    suspend fun setTripDetails(title: String, startDate: String?, note: String?)

    suspend fun setDaysOfTrip(daysLabels: List<String>)

    suspend fun removeDayFromTrip(index: Int)

    suspend fun getNewContributorByUsername(username: String)

    suspend fun selectUnselectContributor(
        uid: String
    )

    suspend fun setUserPermission(userUid: String,permissionToUpdate: Boolean)

    suspend fun addContributor(contributor: ContributorSaveTripDomainModel)

    suspend fun uploadTripToRemoteDatabase()

    suspend fun uploadTripToLocalDatabase()
}