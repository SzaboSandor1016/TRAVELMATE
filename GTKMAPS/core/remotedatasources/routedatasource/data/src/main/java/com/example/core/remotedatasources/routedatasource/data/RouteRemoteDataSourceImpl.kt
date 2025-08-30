package com.example.core.remotedatasources.routedatasource.data

import android.util.Log
import com.example.core.remotedatasources.routedatasource.domain.RouteRemoteDataSource
import com.example.remotedatasources.ORSConstants.Companion.API_KEY
import com.example.remotedatasources.ORSConstants.Companion.API_URL
import com.example.remotedatasources.Requests
import com.example.remotedatasources.responses.RouteResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RouteRemoteDataSourceImpl: RouteRemoteDataSource {

    private val routeServiceRetrofit = Retrofit.Builder()
        .baseUrl(API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(Requests.RouteServiceApi::class.java)

    private var requestCounter = 0 //counter to keep track of the number of requests

    /** [getRouteNode]
     * get the route between two points with transport modes
     * both walking and driving a car
     * return A [com.example.travel_mate.data.RouteNode] that has all the info necessary (see [com.example.travel_mate.data.RouteNode] class)
     */
    override suspend fun getRouteNode(
        startLat: Double,
        startLon: Double,
        endLat: Double,
        endLon: Double
    ): Pair<RouteResponse, RouteResponse> {

        return withContext(Dispatchers.IO) {

            val request = Requests.DirectionsRequest(
                coordinates = listOf(
                    listOf(startLon, startLat),
                    listOf(endLon, endLat)
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

            return@withContext Pair(routeResponseWalk.await(),routeResponseCar.await())
        }
    }

    /*companion object {
        private const val API_KEY: String =
            "5b3ce3597851110001cf6248ee3ca87d76480e20443758659eefacc3c40cad2f9cc7ed033babdc11" // Cseréld le a saját API kulcsodra
        private const val API_URL: String = "https://api.openrouteservice.org/"
    }*/


}