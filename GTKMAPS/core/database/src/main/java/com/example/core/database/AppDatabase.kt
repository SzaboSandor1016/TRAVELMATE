package com.example.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.core.databse.dao.TripDao
import com.example.core.databse.dao.models.AddressLocalEntityModel
import com.example.core.databse.dao.models.CoordinatesLocalEntityModel
import com.example.core.databse.dao.models.DayOfTripLocalEntityModel
import com.example.core.databse.dao.models.DayPlacesCrossRef
import com.example.core.databse.dao.models.PlaceLocalEntityModel
import com.example.core.databse.dao.models.TripLocalEntityModel

/** [AppDatabase]
 *  Abstract class of the [Room] database
 *  defines a function to get the [Dao] (Data access object) of the database
 *
 *  !Any changes made in the data of the [Entity] classes result a need for migration!
 */
@Database(entities = [TripLocalEntityModel::class, PlaceLocalEntityModel::class, DayOfTripLocalEntityModel::class, CoordinatesLocalEntityModel::class, AddressLocalEntityModel::class, DayPlacesCrossRef::class], version = 2)
abstract class AppDatabase: RoomDatabase() {
    abstract fun tripDao(): TripDao
}