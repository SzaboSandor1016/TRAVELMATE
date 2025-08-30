package com.example.core.databse.dao.models

import androidx.room.Entity

@Entity(primaryKeys = ["day_id", "place_uuid"])
data class DayPlacesCrossRef (
    val day_id: String,
    val place_uuid: String
)