package com.example.travel_mate.data

/** [FirebaseRemoteDataSource]
 * interface for the [FirebaseRemoteDataSourceImpl] class
 */
interface FirebaseRemoteDataSource {

    suspend fun uploadTrip(trip: Trip, firebaseIdentifier: TripRepositoryImpl.TripIdentifier)

    suspend fun deleteTrip(uuid: String)

    suspend fun findTripById(uuid: String): Trip

    suspend fun fetchMyTrips(uid: String): List<TripRepositoryImpl.TripIdentifier?>

    suspend fun fetchContributedTrips(uid: String): List<TripRepositoryImpl.TripIdentifier?>

    suspend fun deleteTripsByUserUid(uid: String)

    suspend fun deleteUidFromContributedTrips(uid: String, tripUUID: String)
}