package com.example.travel_mate

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**[com.example.travel_mate.Coordinates]
 * The coordinates of a place
 * has
 *  * - an coordinateId (required for [Room])
 *  * - a latitude [Double]
 *  * - a longitude [Double]
 *  * attribute and [get] and [set] methods for each one
 */
@Entity(tableName = "coordinates" )
class Coordinates() : java.io.Serializable {
    @PrimaryKey(autoGenerate = true) private var coordinateId: Int? = null
    @ColumnInfo(name = "latitude") private var latitude: Double = 0.0
    @ColumnInfo(name = "longitude") private var longitude: Double = 0.0

    constructor(latitude: Double,longitude: Double) : this(){
        this.latitude = latitude
        this.longitude = longitude
    }
    constructor(coordinateId: Int, latitude: Double,longitude: Double) : this(){
        this.coordinateId = coordinateId
        this.latitude = latitude
        this.longitude = longitude
    }
    fun setCoordinateId(coordinateId: Int){
        this.coordinateId = coordinateId
    }
    fun getCoordinateId(): Int?{
        return this.coordinateId
    }
    fun setLatitude(latitude: Double){
        this.latitude = latitude
    }
    fun getLatitude(): Double{
        return this.latitude
    }
    fun setLongitude(longitude: Double){
        this.longitude = longitude
    }
    fun getLongitude(): Double{
        return this.longitude
    }

}