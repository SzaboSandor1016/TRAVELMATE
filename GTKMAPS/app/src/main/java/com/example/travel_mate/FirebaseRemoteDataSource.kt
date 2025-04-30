package com.example.travel_mate

import com.example.travel_mate.TripRepository.TripIdentifier

/** [FirebaseRemoteDataSource]
 * interface for the [FirebaseRemoteDataSourceImpl] class
 */
interface FirebaseRemoteDataSource {

    suspend fun uploadTrip(trip: Trip, firebaseIdentifier: TripIdentifier)

    suspend fun deleteTrip(uuid: String)

    suspend fun findTripById(uuid: String): Trip

    suspend fun fetchMyTrips(uid: String): List<TripIdentifier?>

    suspend fun fetchContributedTrips(uid: String): List<TripIdentifier?>

    suspend fun deleteTripsByUserUid(uid: String)

    suspend fun deleteUidFromContributedTrips(uid: String, tripUUID: String)
}