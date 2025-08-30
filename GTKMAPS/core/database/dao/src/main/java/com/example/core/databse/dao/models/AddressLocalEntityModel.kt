package com.example.core.databse.dao.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "address_of_place")
data class AddressLocalEntityModel(
    @PrimaryKey(autoGenerate = true) val addressId: Long? = null,
    @ColumnInfo(name = "city") val city: String?,
    @ColumnInfo(name = "street") val street: String?,
    @ColumnInfo(name = "house_number") val houseNumber: String?,
    @ColumnInfo(name = "country") val country: String?
) : Serializable