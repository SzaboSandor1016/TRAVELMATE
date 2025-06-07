package com.example.travel_mate.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction

@Dao
interface TripDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    fun uploadTrip(trip: Trip, places: List<Place>)

    @Delete
    fun deleteTrip(trip: Trip, places: List<Place>)

    @Query("SELECT id as uuid, trip_title as title FROM trips")
    fun getAllTripIdentifiers(): List<TripRepositoryImpl.TripIdentifier>

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