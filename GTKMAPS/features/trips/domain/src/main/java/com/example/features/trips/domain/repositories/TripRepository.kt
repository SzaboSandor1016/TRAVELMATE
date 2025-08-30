package com.example.features.trips.domain.repositories

import com.example.features.trips.domain.models.TripIdentifierTripsDomainModel
import com.example.features.trips.domain.models.TripTripsDomainModel
import kotlinx.coroutines.flow.Flow

interface TripRepository {

    //val tripsStateFlow: StateFlow<TripsState>

    //suspend fun saveNewTrip(trip: Trip, tripIdentifier: TripIdentifier)

    //suspend fun saveTripWithUpdatedPlaces(startPlace: Place, places: List<Place>)

    fun hasUserSignedIn(): Flow<Boolean>

    fun getCurrentUserId(): Flow<String?>

    //suspend fun deleteUserTripsFromRemoteDatabase(userUid: String)

    suspend fun deleteCurrentTripFromRemoteDatabase(tripUuid: String)

    suspend fun deleteUidFromContributedTrips(uid: String, tripUuid: String)

    suspend fun getCurrentRemoteTripData(
        remoteTripUUID: String
    ): Flow<TripTripsDomainModel>

    suspend fun fetchMyTripsFromFirebase(
        //userUid: String
    ): Flow<List<TripIdentifierTripsDomainModel.Remote>>

    suspend fun fetchContributedTripsFromFirebase(
        //userUid: String
    ): Flow<List<TripIdentifierTripsDomainModel.Remote>>

    suspend fun deleteCurrentTripFromLocalDatabase(tripUID: String)

    suspend fun fetchAllLocalSavedTrips(): Flow<List<TripIdentifierTripsDomainModel.Local>>

    suspend fun getCurrentLocalTripData(
        localTripUUID: String
    ): TripTripsDomainModel.Local
}