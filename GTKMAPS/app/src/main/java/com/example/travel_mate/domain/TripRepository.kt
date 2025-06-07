package com.example.travel_mate.domain

import com.example.travel_mate.data.Place
import com.example.travel_mate.data.Trip
import com.example.travel_mate.data.TripRepositoryImpl.TripIdentifier
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface TripRepository {

    //val tripsStateFlow: StateFlow<TripsState>

    //suspend fun saveNewTrip(trip: Trip, tripIdentifier: TripIdentifier)

    //suspend fun saveTripWithUpdatedPlaces(startPlace: Place, places: List<Place>)

    suspend fun deleteUserTripsFromRemoteDatabase(userUid: String)

    suspend fun uploadTripToRemoteDatabase(userUid: String, trip: Trip, tripIdentifier: TripIdentifier)

    suspend fun uploadContributedTripToRemoteDatabase(trip: Trip, tripIdentifier: TripIdentifier)

    suspend fun deleteCurrentTripFromRemoteDatabase(tripUuid: String)

    suspend fun deleteUidFromContributedTrips(uid: String, tripUuid: String)

    suspend fun getCurrentRemoteTripData(tripIdentifier: TripIdentifier ): Flow<Trip>

    suspend fun fetchMyTripsFromFirebase(firebaseUser: FirebaseUser): Flow<List<TripIdentifier>>

    suspend fun fetchContributedTripsFromFirebase(userUid: String): Flow<List<TripIdentifier>>

    suspend fun deleteCurrentTripFromLocalDatabase(trip: Trip)

    suspend fun fetchAllLocalSavedTrips(): Flow<List<TripIdentifier>>

    suspend fun uploadTripToLocalDatabase(trip: Trip)

    suspend fun getCurrentLocalTripData( tripIdentifier: TripIdentifier): Flow<Trip>




}