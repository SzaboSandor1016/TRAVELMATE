package com.example.core.databse.dao.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "days_of_trips")
data class DayOfTripLocalEntityModel(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "day_id") var dayUUID: String,
    @ColumnInfo(name = "trip_id") val tripId: String,
    @ColumnInfo(name = "label") val label: String
) {
}