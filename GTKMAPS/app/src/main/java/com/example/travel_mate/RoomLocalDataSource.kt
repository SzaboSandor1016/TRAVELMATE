package com.example.travel_mate

/** [RoomLocalDataSource]
 * interface for the [RoomLocalDataSourceImpl] class
 */
interface RoomLocalDataSource {

    suspend fun uploadTrip(trip: Trip)

    suspend fun deleteTrip(trip: Trip)

    suspend fun findTripById(uuid: String): Trip

    suspend fun fetchTripIdentifiers(): List<TripRepository.TripIdentifier>
}