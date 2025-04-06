package com.example.travel_mate

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

/** [AppDatabase]
 *  Abstract class of the [Room] database
 *  defines a function to get the [Dao] (Data access object) of the database
 *
 *  !Any changes made in the data of the [Entity] classes result a need for migration!
 */
@Database(entities = [Trip::class, Place::class, Coordinates::class, Address::class], version = 2)
abstract class AppDatabase: RoomDatabase() {
    abstract fun tripDao(): TripDao
}