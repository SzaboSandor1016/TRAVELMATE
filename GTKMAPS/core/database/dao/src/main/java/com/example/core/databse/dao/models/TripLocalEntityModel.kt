package com.example.core.databse.dao.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "trips")
data class TripLocalEntityModel(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var uUID: String,
    @Embedded var startPlace: PlaceLocalEntityModel,
    @ColumnInfo(name = "date_of_trip") var date: String?,
    @ColumnInfo(name = "trip_title") var title: String,
    @ColumnInfo(name = "trip_note") var note: String?,
): Serializable