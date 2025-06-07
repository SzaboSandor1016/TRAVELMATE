package com.example.travel_mate.data

/** [RoomLocalDataSource]
 * interface for the [RoomLocalDataSourceImpl] class
 */
interface RoomLocalDataSource {

    suspend fun uploadTrip(trip: Trip)

    suspend fun deleteTrip(trip: Trip)

    suspend fun findTripById(uuid: String): Trip

    suspend fun fetchTripIdentifiers(): List<TripRepositoryImpl.TripIdentifier>
}