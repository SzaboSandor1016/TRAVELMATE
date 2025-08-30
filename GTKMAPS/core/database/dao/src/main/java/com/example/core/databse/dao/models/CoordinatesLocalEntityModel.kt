package com.example.core.databse.dao.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "coordinate_of_place")
data class CoordinatesLocalEntityModel(
    @PrimaryKey(autoGenerate = true) val coordinatesId: Long? = null,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double
) : Serializable