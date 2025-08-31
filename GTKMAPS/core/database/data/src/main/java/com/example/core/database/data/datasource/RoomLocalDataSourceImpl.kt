package com.example.core.database.data.datasource

import com.example.core.database.domain.datasource.RoomLocalDataSource
import com.example.core.databse.dao.TripDao
import com.example.core.databse.dao.models.DayOfTripLocalEntityModel
import com.example.core.databse.dao.models.PlaceLocalEntityModel
import com.example.core.databse.dao.models.TripLocalEntityModel
import com.example.core.databse.dao.models.TripIdentifierLocalEntityModel
import kotlinx.coroutines.flow.Flow

class RoomLocalDataSourceImpl(
    private val tripDao: TripDao
): RoomLocalDataSource {
    
    override fun uploadTrip(
        trip: TripLocalEntityModel,
        days: List<DayOfTripLocalEntityModel>,
        places: List<PlaceLocalEntityModel>,
        originalDays: List<DayOfTripLocalEntityModel>,
        originalPlaces: List<PlaceLocalEntityModel>
    ) {
        deleteOriginalDays(
            days = originalDays,
            places = originalPlaces
        )

        tripDao.uploadTrip(
            trip = trip,
            days = days,
            places = places
        )
    }
    
    override fun deleteTrip(
        uuid: String
    ) {

        val trip = findTripById(uuid)

        tripDao.deleteTrip(
            trip = trip.trip,
            days = trip.days.map { it.day },
            places = trip.days.flatMap { it.places }
        )
    }

    private fun deleteOriginalDays(
        days: List<DayOfTripLocalEntityModel>,
        places: List<PlaceLocalEntityModel>
    ) {
        tripDao.deleteOriginalDays(
            days = days,
            places = places
        )
    }
    
    override fun findTripById(uuid: String): TripDao.TripWithDaysAndPlaces {

        return tripDao.findTripByUUID(
                uuid = uuid
            )
    }
    
    override fun fetchTripIdentifiers(): Flow<List<TripIdentifierLocalEntityModel>> {

        return tripDao.getAllTripIdentifiers()
    }
}