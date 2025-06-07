package com.example.travel_mate.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class RouteUtilityClass {
    private val routeUtilityCoroutineDispatcher = Dispatchers.IO

    public suspend fun haversine(startLat: Double, startLon: Double, endLat: Double, endLon: Double): Double {

        return withContext(routeUtilityCoroutineDispatcher) {

            var lat1 = Math.toRadians(startLat)
            var lon1 = Math.toRadians(startLon)
            var lat2 = Math.toRadians(endLat)
            var lon2 = Math.toRadians(endLon)

            val dLat = lat2 - lat1
            val dLon = lon2 - lon1

            val a = sin(dLat / 2) * sin(dLat / 2) +
                    cos(lat1) * cos(lat2) * sin(dLon / 2) * sin(dLon / 2)

            val c = 2 * atan2(sqrt(a), sqrt(1 - a))

            val earthRadius = 6371.0
            val distance = earthRadius * c

            return@withContext distance
        }
    }
}