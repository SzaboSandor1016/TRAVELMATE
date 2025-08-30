package com.example.core.databse.dao.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "places")
data class PlaceLocalEntityModel(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "place_uuid") var uUID: String,
    @ColumnInfo(name = "day_uuid") val dayId: String,
    @ColumnInfo(name = "place_name") val name: String?,
    @ColumnInfo(name = "cuisine") val cuisine: String?,
    @ColumnInfo(name = "opening_hours")val openingHours: String?,
    @ColumnInfo(name = "charge")val charge: String?,
    @Embedded val address: AddressLocalEntityModel,
    @Embedded val coordinates: CoordinatesLocalEntityModel,
    @ColumnInfo(name = "category") val category: String,
    ) : Serializable