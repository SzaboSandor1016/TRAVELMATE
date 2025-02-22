package com.example.gtk_maps

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.lang.StringBuilder


class DataRepository{

    companion object {
        @Volatile
        private var INSTANCE: DataRepository? = null

        fun getInstance(): DataRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DataRepository().also {
                    INSTANCE = it
                }
            }
        }
    }

    private fun <T> convertClassToJson(classToConvert: T ): String{

        return Gson().toJson(classToConvert)
    }

    private fun <T> processReadString(readString: String, classType: Class<T>): List<T> {
        val gson = Gson()
        val type = TypeToken.getParameterized(ArrayList::class.java, classType).type
        return gson.fromJson<ArrayList<T>?>(readString, type).toList()
    }

    suspend fun checkFileExists(fileName: String): Boolean{

        return withContext(Dispatchers.IO) {

            return@withContext File(MyApplication.appContext.filesDir, fileName).exists()

        }
    }

    suspend fun <T> writeStorage(classesToWrite: ArrayList<T>, fileName: String){

        withContext(Dispatchers.IO) {

            val contentString = StringBuilder()

            contentString.append("[")

             contentString.append(
                 classesToWrite.joinToString(separator = ",") { convertClassToJson(it) }
             )
            contentString.append("]")

            MyApplication.appContext.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                it.write(contentString.toString().toByteArray())
            }
        }
    }
    suspend fun <T> readStorage(fileName: String, classType: Class<T>): List<T> {

        return withContext(Dispatchers.IO) {
            val file = File(MyApplication.appContext.filesDir, fileName)
            val readString = file.readText()

            processReadString(readString, classType)
        }
    }

    private val overpassRetrofit = Retrofit.Builder()
        .baseUrl("https://overpass-api.de/api/")  // Az Overpass API alap URL-je
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ClassRequests.OverpassApi::class.java)

    suspend fun searchOverpass(query: String, category: String): ArrayList<ClassPlace>{

        return withContext(Dispatchers.IO) {
            try {

                val response = overpassRetrofit.getNearbyPlaces(query)
                processOverpassResponse(
                    response.elements,
                    category
                ) // Process the returned elements
            } catch (e: Exception) {

                Log.e("OverpassSearch", "Failed to fetch places", e)
                ArrayList()
            }
        }
    }

    private fun processOverpassResponse(elements: List<ResponseOverpass.Element>, category: String): ArrayList<ClassPlace> {

        val places: ArrayList<ClassPlace> = ArrayList()

        for (element in elements) {

            val place = ClassPlace()
            val address = ClassAddress()

            // Ha van középpont (way, relation), akkor azt használjuk
            if (element.center != null) {

                val coordinates = ClassCoordinates()
                coordinates.setLatitude(element.center.lat)
                coordinates.setLongitude(element.center.lon)
                place.setCoordinates(coordinates)
            } else {

                val coordinates = ClassCoordinates()
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
            places.add(place)

        }

        return places
    }

    private var client: OkHttpClient = OkHttpClient().newBuilder()
        .addInterceptor(Interceptor { chain: Interceptor.Chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0"
                )
                .header("Accept-Language", "hu-HU,hu;q=0.8,en-US;q=0.5,en;q=0.3")
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        })
        .build()

    val photonRetrofit: ClassRequests.PhotonApi = Retrofit.Builder()
        .baseUrl("https://photon.komoot.io/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ClassRequests.PhotonApi::class.java)

    suspend fun searchAutocomplete(query: String): ArrayList<ClassPlace> {

        return withContext(Dispatchers.IO) {

            try {
                val response = photonRetrofit.getAutocomplete(query,"4")
                return@withContext processPhotonResponse(response.features)
            } catch (e: Exception) {

                Log.e("ReverseGeoCode", "Failed to fetch reverse geocode", e)
                ArrayList()
            }

        }
    }

    suspend fun searchReverseGeoCode(coordinates: ClassCoordinates): ArrayList<ClassPlace>{

        return withContext(Dispatchers.IO) {
            try {
                val response = photonRetrofit.getReverseGeoCode(
                    coordinates.getLatitude().toString(),
                    coordinates.getLongitude().toString()
                )
                return@withContext processPhotonResponse(response.features)
            } catch (e: Exception) {

                Log.e("ReverseGeoCode", "Failed to fetch reverse geocode", e)
                ArrayList()
            }
        }
    }

    private fun processPhotonResponse(response: List<ResponsePhoton.Feature>): ArrayList<ClassPlace> {

        val places: ArrayList<ClassPlace> = ArrayList()

        for (feature in response) {

            val startPlace = ClassPlace()
            val placeCoordinates = ClassCoordinates()
            val address = ClassAddress()

            if (feature.geometry != null) {

                val geometry = feature.geometry

                if (geometry != null) {

                    val coordinates = geometry.coordinates

                    if (coordinates != null) {
                        placeCoordinates.setLatitude(coordinates[1].toDouble())
                        placeCoordinates.setLongitude(coordinates[0].toDouble())
                    }
                }

            }
            if (feature.properties != null) {

                val properties = feature.properties

                if (properties.name != null) {
                    startPlace.setName(properties.name)
                    Log.d("Address", properties.name.toString())
                }
                if (properties.city != null) {
                    address.setCity(properties.city)
                    Log.d("Address_city", properties.city.toString())
                }
                if (properties.street != null) {
                    address.setStreet(properties.street)
                    Log.d("Address_street", properties.street.toString())
                }
                if (properties.houseNumber != null) {
                    address.setHouseNumber(properties.houseNumber)
                    Log.d("Address_hn", properties.houseNumber.toString())
                }
                if (properties.country != null) {
                    address.setCountry(properties.country)
                }

            }

            startPlace.setAddress(address)
            startPlace.setCoordinates(placeCoordinates)
            places.add(startPlace)


        }
        return places
    }



}
