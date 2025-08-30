package com.example.core.remotedatasources.searchplacesdatasource.data

import com.example.core.remotedatasources.searchplacesdatasource.domain.OverpassRemoteDataSource
import com.example.remotedatasources.Requests
import com.example.remotedatasources.responses.OverpassResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OverpassRemoteDataSourceImpl: OverpassRemoteDataSource {

    private val overpassRetrofit = Retrofit.Builder()
        .baseUrl("https://overpass-api.de/api/")  // Az Overpass API alap URL-je
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(Requests.OverpassApi::class.java)

    /** [fetchPlacesByCity]
     * the default search mode for nearby places
     * Uses the city attribute of the [com.example.model.Search]'s start place to fetch
     * the places that are in that city
     */
    override suspend fun fetchPlacesByCity(
        content: String,
        city: String,
        category: String
    ): Flow<OverpassResponse> {

        val query = getNearbyUrl(content, city)

        return flowOf(overpassRetrofit.getNearbyPlaces(query))

        /*return withContext(Dispatchers.IO) {



            try {
                val response =

                processOverpassResponse(
                    response.elements,
                    category
                )
            } catch (e: Exception) {

                Log.e("OverpassSearch", "Failed to fetch places", e)
                emptyList()
            }

        }*/
    }

    /** [fetchPlacesByCoordinates]
     * fetch the places by their maximum distance to the [com.example.model.Search]'s start place
     * if there is a custom transport mode and searching distance selected
     */
    override suspend fun fetchPlacesByCoordinates(
        content: String,
        lat: String,
        lon: String,
        dist: Double,
        category: String
    ): Flow<OverpassResponse> {

        val query = getNearbyUrl(content, lat, lon, dist)

        return flowOf(overpassRetrofit.getNearbyPlaces(query))

        /*return withContext(Dispatchers.IO) {



            *//*try {




                *//**//*processOverpassResponse(
                    response.elements,
                    category
                )*//**//*
            } catch (e: Exception) {

                Log.e("OverpassSearch", "Failed to fetch places", e)
                emptyList()
            }*//*
        }*/
    }

    private fun getNearbyUrl(
        content: String,
        lat: String,
        lon: String,
        dist: Double
    ): String{

        val splitContent: List<String> = content.split(";")
        var fullString = ""

        for (string in splitContent) {

            var baseString = "nwr[;;](around:dist,startLat,startLong);"

            baseString = baseString.replace(";;", string, true)
            fullString += baseString
        }

        var baseurl = "[out:json];($fullString);out center;";

        baseurl = baseurl.replace("dist", dist.toString());
        baseurl = baseurl.replace("startLat", lat);
        baseurl = baseurl.replace("startLong", lon);

        return  baseurl;
    }
    private fun getNearbyUrl(
        content: String,
        city: String
    ): String{

        val splitContent: List<String> = content.split(";")
        var fullString = ""
        for (string in splitContent) {

            var baseString = "nwr[;;][\"addr:city\"=\"$city\"];"

            baseString = baseString.replace(";;", string, true)

            fullString += baseString
        }

        val baseurl = "[out:json];($fullString);out center;";

        return  baseurl;
    }

}