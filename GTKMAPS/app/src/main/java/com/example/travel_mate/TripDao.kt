package com.example.travel_mate

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction

@Dao
interface TripDao {

    @Insert(onConflict = REPLACE)
    fun uploadTrip(trip: Trip, places: List<Place>)

    @Delete
    fun deleteTrip(trip: Trip, places: List<Place>)

    @Query("SELECT id as uuid, trip_title as title FROM trips")
    fun getAllTripIdentifiers(): List<TripRepository.TripIdentifier>

    @Transaction
    @Query("SELECT * " +
            "FROM trips " +
            "WHERE trips.id LIKE :uuid")
    fun findTripByUUID(uuid: String): TripWithPlaces

    data class TripWithPlaces(
        @Embedded val trips: Trip,
        @Relation(
            parentColumn = "id",
            entityColumn = "trip_id"
        )
        val places: List<Place>
    )

}