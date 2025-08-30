package com.example.core.database.domain.datasource

import com.example.core.databse.dao.TripDao
import com.example.core.databse.dao.models.DayOfTripLocalEntityModel
import com.example.core.databse.dao.models.PlaceLocalEntityModel
import com.example.core.databse.dao.models.TripLocalEntityModel
import com.example.core.databse.dao.models.TripIdentifierLocalEntityModel
import kotlinx.coroutines.flow.Flow

/** [RoomLocalDataSource]
 * interface for the [com.example.data.datasources.RoomLocalDataSourceImpl] class
 */
interface RoomLocalDataSource {

    fun uploadTrip(
        trip: TripLocalEntityModel,
        days: List<DayOfTripLocalEntityModel>,
        places: List<PlaceLocalEntityModel>,
        originalDays: List<DayOfTripLocalEntityModel>,
        originalPlaces: List<PlaceLocalEntityModel>
    )

    fun deleteTrip(uuid: String)

    fun findTripById(uuid: String): TripDao.TripWithDaysAndPlaces

    fun fetchTripIdentifiers(): Flow<List<TripIdentifierLocalEntityModel>>
}