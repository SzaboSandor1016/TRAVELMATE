package com.example.travel_mate

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OverpassRemoteDataSourceImpl: OverpassRemoteDataSource {

    private val overpassRetrofit = Retrofit.Builder()
        .baseUrl("https://overpass-api.de/api/")  // Az Overpass API alap URL-je
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ClassRequests.OverpassApi::class.java)

    /** [processOverpassResponse]
     * process the response of an Overpass network request
     * create a [List] of [Place]s from the [OverpassResponse]
     */
    private fun processOverpassResponse(elements: List<OverpassResponse.Element>, category: String): List<Place> {

        var places: List<Place> = emptyList()

        for (element in elements) {

            val place = Place()
            val address = Address()

            place.setUUId()

            // Ha van középpont (way, relation), akkor azt használjuk
            if (element.center != null) {

                val coordinates = Coordinates()
                coordinates.setLatitude(element.center.lat)
                coordinates.setLongitude(element.center.lon)
                place.setCoordinates(coordinates)
            } else {

                val coordinates = Coordinates()
                coordinates.setLatitude(element.lat)
                coordinates.setLongitude(element.lon)
                place.setCoordinates(coordinates)
            }

            place.setCategory(category)
            val tags = element.tags ?: emptyMap()

            place.setName(

                when {
                    tags.containsKey("name") -> tags["name"]!!
                    else -> "Ismeretlen"
                }
            )

            if (tags.containsKey("cuisine")) place.setCuisine(tags["cuisine"]!!)
            if (tags.containsKey("opening_hours")) place.setOpeningHours(tags["opening_hours"]!!)
            if (tags.containsKey("charge")) place.setCharge(tags["charge"]!!)
            if (tags.containsKey("addr:city")) address.setCity(tags["addr:city"]!!)
            if (tags.containsKey("addr:street")) address.setStreet(tags["addr:street"]!!)
            if (tags.containsKey("addr:housenumber")) address.setHouseNumber(tags["addr:housenumber"]!!)

            Log.d("name", place.getName()!!)

            place.setAddress(address)
            places = places.plus(place)

        }

        return places
    }

    /** [fetchPlacesByCity]
     * the default search mode for nearby places
     * Uses the city attribute of the [Search]'s start place to fetch
     * the places that are in that city
     */
    override suspend fun fetchPlacesByCity(
        content: String,
        city: String,
        category: String
    ): List<Place> {

        return withContext(Dispatchers.IO) {

            val query = getNearbyUrl(content,city)

            try {
                val response = overpassRetrofit.getNearbyPlaces(query)

                processOverpassResponse(
                    response.elements,
                    category)
            } catch (e: Exception) {

                Log.e("OverpassSearch", "Failed to fetch places", e)
                emptyList()
            }

        }
    }

    /** [fetchPlacesByCoordinates]
     * fetch the places by their maximum distance to the [Search]'s start place
     * if there is a custom transport mode and searching distance selected
     */
    override suspend fun fetchPlacesByCoordinates(
        content: String,
        lat: String,
        lon: String,
        dist: Double,
        category: String
    ): List<Place> {
        return withContext(Dispatchers.IO) {

            try {
                val query = getNearbyUrl(content, lat, lon, dist)

                val response = overpassRetrofit.getNearbyPlaces(query)

                processOverpassResponse(
                    response.elements,
                    category
                )
            } catch (e: Exception) {

                Log.e("OverpassSearch", "Failed to fetch places", e)
                emptyList()
            }
        }
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