package com.example.travel_mate.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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

            val request = DirectionsRequest(
                coordinates = listOf(
                    listOf(pointStart.getLongitude(), pointStart.getLatitude()),
                    listOf(pointEnd.getLongitude(), pointEnd.getLatitude())
                )
            )

            val routeResponseWalk = async {

                routeServiceRetrofit.getRoute(
                    apiKey = API_KEY,
                    profile = "foot-walking",
                    request = request
                )
            }
            val routeResponseCar = async {

                routeServiceRetrofit.getRoute(
                    apiKey = API_KEY,
                    profile = "driving-car",
                    request = request
                )
            }

            requestCounter = requestCounter + 2

            Log.d("RequestCounter", requestCounter.toString())

            return@withContext processRouteResponse(
                pointEnd,
                routeResponseWalk.await(),
                routeResponseCar.await()
            )
        }
    }

    override suspend fun getReverseGeoCode(coordinates: Coordinates): Flow<ReverseGeoCodeResponse> {


        return flowOf(
            routeServiceRetrofit.getReverseGeoCode(
                apiKey = API_KEY,
                longitude = coordinates.getLongitude(),
                latitude = coordinates.getLatitude(),
                size = 1
            )
        )
    }

    /** [processRouteResponse]
     * process the response of a OpenRouteService network request
     * create [RouteNode] from the [RouteResponse]
     */
    private fun processRouteResponse(coordinates: Coordinates, routeResponseWalk: RouteResponse, routeResponseCar: RouteResponse): RouteNode {

        val walkRoutePolyLine = Polyline()
        val carRoutePolyLine = Polyline()

        var walkSteps: ArrayList<RouteStep> = ArrayList()
        var carSteps: ArrayList<RouteStep> = ArrayList()

        var distanceWalk = 0
        var distanceCar = 0

        var durationWalk = 0
        var durationCar = 0

        routeResponseWalk.routes.forEach {

            val points: MutableList<MutableList<Double>> =
                decodeGeometry(it.geometry)

            Log.d("count", points.size.toString())

            for (point in points) {
                val lat: Double = point[0]
                val lon: Double = point[1]
                walkRoutePolyLine.addPoint(GeoPoint(lat, lon))
                walkSteps.add(
                    RouteStep(
                        coordinates = Coordinates(
                            latitude = lat,
                            longitude = lon
                        )
                    )
                )
            }

            for (step in it.segments[0].steps) {

                walkSteps[step.wayPoints[0]!!] = walkSteps[step.wayPoints[0]!!].copy(
                    distance = step.distance.toInt(),
                    duration = step.duration.toInt(),
                    name = step.name,
                    instruction = step.instruction,
                    type = step.type
                )

            }

            distanceWalk = it.summary.distance.toInt()

            durationWalk = (it.summary.duration / 60).toInt()

        }

        routeResponseCar.routes.forEach {

            /*it.properties.segments.forEach {
                it.steps.forEach {
                    it.
                }
            }*/

            val points: MutableList<MutableList<Double>> =
                decodeGeometry(it.geometry)

            Log.d("count", points.size.toString())

            for (point in points) {
                val lat: Double = point[0]
                val lon: Double = point[1]
                carRoutePolyLine.addPoint(GeoPoint(lat, lon))
                carSteps.add(
                    RouteStep(
                        coordinates = Coordinates(
                            latitude = lat,
                            longitude = lon
                        )
                    )
                )
            }
            for (step in it.segments[0].steps) {
                carSteps[step.wayPoints[0]] = carSteps[step.wayPoints[0]].copy(
                    distance = step.distance.toInt(),
                    duration = step.duration.toInt(),
                    name = step.name,
                    instruction = step.instruction,
                    type = step.type
                )

            }

            distanceCar = it.summary.distance.toInt()

            durationCar = (it.summary.duration / 60).toInt()

        }

        return RouteNode(
            walkPolyLine = walkRoutePolyLine,
            carPolyLine = carRoutePolyLine,
            walkDistance = distanceWalk,
            walkDuration = durationWalk,
            carDistance = distanceCar,
            carDuration = durationCar,
            coordinate = coordinates,
            carRouteSteps = carSteps.toList(),
            walkRouteSteps = walkSteps.toList()
        )
    }

    /** [decodeGeometry]
     * decodes the geometry "encoded polyline" [String]
     * containing the coordinates of the [Route]'s polyline returned
     * by the [getRouteNode] request.
     *
     */
    private fun decodeGeometry(encodedGeometry: String): MutableList<MutableList<Double>> {

        val geometry: MutableList<MutableList<Double>> = ArrayList<MutableList<Double>>()
        var index = 0
        val len = encodedGeometry.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var result = 1
            var shift = 0
            var b: Int
            do {
                b = encodedGeometry[index++].code - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lat += if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)

            result = 1
            shift = 0
            do {
                b = encodedGeometry[index++].code - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lng += if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)

            val point: MutableList<Double> = ArrayList<Double>()
            point.add(lat / 1E5)
            point.add(lng / 1E5)
            geometry.add(point)
            Log.d("coordinates", point[0].toString() + " , " + point[1].toString())
        }

        return geometry
    }

    /** [DirectionsRequest]
     *  Body data class for retrofit,
     *  where one may specify extra query options for ORSM
     */
    data class DirectionsRequest(
        val coordinates: List<List<Double>>,
        val language: String = "hu-hu"
    )

    companion object {
        private const val API_KEY: String =
            "5b3ce3597851110001cf6248ee3ca87d76480e20443758659eefacc3c40cad2f9cc7ed033babdc11" // Cseréld le a saját API kulcsodra
        private const val API_URL: String = "https://api.openrouteservice.org/"
    }


}