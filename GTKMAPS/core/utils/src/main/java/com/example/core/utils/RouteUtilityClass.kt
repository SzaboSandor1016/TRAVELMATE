package com.example.core.utils

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

            val lat1 = Math.toRadians(startLat)
            val lon1 = Math.toRadians(startLon)
            val lat2 = Math.toRadians(endLat)
            val lon2 = Math.toRadians(endLon)

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