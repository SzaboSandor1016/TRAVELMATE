package com.example.core.databse.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Junction
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.example.core.databse.dao.models.DayOfTripLocalEntityModel
import com.example.core.databse.dao.models.DayPlacesCrossRef
import com.example.core.databse.dao.models.PlaceLocalEntityModel
import com.example.core.databse.dao.models.TripIdentifierLocalEntityModel
import com.example.core.databse.dao.models.TripLocalEntityModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    fun uploadTrip(trip: TripLocalEntityModel, days: List<DayOfTripLocalEntityModel>, places: List<PlaceLocalEntityModel>)

    @Delete
    fun deleteTrip(trip: TripLocalEntityModel, days: List<DayOfTripLocalEntityModel>, places: List<PlaceLocalEntityModel>)

    @Delete
    fun deleteOriginalDays(days: List<DayOfTripLocalEntityModel>, places: List<PlaceLocalEntityModel>)

    @Query("SELECT id as uuid, trip_title as title FROM trips")
    fun getAllTripIdentifiers(): Flow<List<TripIdentifierLocalEntityModel>>

    @Transaction
    @Query("SELECT * " +
            "FROM trips " +
            "WHERE trips.id LIKE :uuid")
    fun findTripByUUID(uuid: String): TripWithDaysAndPlaces

    data class TripWithDaysAndPlaces(
        @Embedded val trip: TripLocalEntityModel,
        @Relation(
            entity = DayOfTripLocalEntityModel::class,
            parentColumn = "id",
            entityColumn = "trip_id"
        )
        val days: List<DayWithPlaces>
    )

    data class DayWithPlaces(
        @Embedded val day: DayOfTripLocalEntityModel,
        @Relation(
            parentColumn = "day_id",
            entityColumn = "day_uuid"
        )
        val places: List<PlaceLocalEntityModel>
    )
}