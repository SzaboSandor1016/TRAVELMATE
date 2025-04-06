package com.example.travel_mate

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RouteRemoteDataSourceImpl: RouteRemoteDataSource {

    private val routeServiceRetrofit = Retrofit.Builder()
        .baseUrl(API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ClassRequests.RouteServiceApi::class.java)

    private var requestCounter = 0 //counter to keep track of the number of requests

    /** [getRouteNode]
     * get the route between two points with transport modes
     * both walking and driving a car
     * return A [RouteNode] that has all the info necessary (see [RouteNode] class)
     */
    override suspend fun getRouteNode(
        pointStart: Coordinates,
        pointEnd: Coordinates
    ): RouteNode {

        return withContext(Dispatchers.IO) {

            val routeResponseWalk = async { routeServiceRetrofit.getRoute(
                profile = "foot-walking",
                apiKey = API_KEY,
                start = pointStart.getLongitude().toString() + "," + pointStart.getLatitude().toString(),
                end = pointEnd.getLongitude().toString() + "," + pointEnd.getLatitude().toString()
            ) }
            val routeResponseCar = async {  routeServiceRetrofit.getRoute(
                profile = "driving-car",
                apiKey = API_KEY,
                start = pointStart.getLongitude().toString() + "," + pointStart.getLatitude().toString(),
                end = pointEnd.getLongitude().toString() + "," + pointEnd.getLatitude().toString()
            ) }

            requestCounter = requestCounter + 2

            Log.d("RequestCounter", requestCounter.toString() )

            return@withContext processRouteResponse(pointEnd, routeResponseWalk.await(), routeResponseCar.await())
        }
    }
    /** [processRouteResponse]
     * process the response of a OpenRouteService network request
     * create [RouteNode] from the [RouteResponse]
     */
    private fun processRouteResponse(coordinates: Coordinates, routeResponseWalk: RouteResponse, routeResponseCar: RouteResponse): RouteNode {

        val walkRoutePolyLine = Polyline()
        val carRoutePolyLine = Polyline()

        var distanceWalk = 0
        var distanceCar = 0

        var durationWalk = 0
        var durationCar = 0

        routeResponseWalk.features.forEach {

            it.geometry.coordinates.forEach {
                walkRoutePolyLine.addPoint(GeoPoint(it[1],it[0]))

                Log.d("routePolysPoints", it[1] .toString()+ "," +it[0].toString())
            }

            distanceWalk = it.properties.summary.distance.toInt()

            durationWalk = (it.properties.summary.duration / 60).toInt()

        }

        routeResponseCar.features.forEach {

            it.geometry.coordinates.forEach {
                carRoutePolyLine.addPoint(GeoPoint(it[1],it[0]))

                Log.d("routePolysPoints", it[1] .toString()+ "," +it[0].toString())
            }

            distanceCar = it.properties.summary.distance.toInt()

            durationCar = (it.properties.summary.duration / 60).toInt()

        }

        return RouteNode(
            walkPolyLine = walkRoutePolyLine,
            carPolyLine = carRoutePolyLine,
            walkDistance = distanceWalk,
            walkDuration = durationWalk,
            carDistance = distanceCar,
            carDuration = durationCar,
            coordinate = coordinates
        )
    }

    companion object {
        private const val API_KEY: String =
            "5b3ce3597851110001cf624864449e6dcee94ccca17b179e95f464dc" // Cseréld le a saját API kulcsodra
        private const val API_URL: String = "https://api.openrouteservice.org/"
    }


}