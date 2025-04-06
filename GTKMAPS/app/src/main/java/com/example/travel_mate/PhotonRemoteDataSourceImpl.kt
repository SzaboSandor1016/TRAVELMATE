package com.example.travel_mate

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PhotonRemoteDataSourceImpl: PhotonRemoteDataSource {

    /**
     * a client for the request made with Photon for autocomplete
     * or reverse geoCode to find the address of the current location of the user
     * If this is not set the data source results inconsistent or wrong data
     * when searching autocomplete
     *
     * ps. feel free to try it just comment the .client() part from [photonRetrofit] builder
     */
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

    /** [processPhotonResponse]
     * process the data returned by the network request to Photon
     * accepts the feature list of [PhotonResponse]
     * Returns a list of [Place]s
     */
    private fun processPhotonResponse(response: List<PhotonResponse.Feature>): ArrayList<Place> {

        val places: ArrayList<Place> = ArrayList()

        for (feature in response) {

            val startPlace = Place()
            val placeCoordinates = Coordinates()
            val address = Address()

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

            startPlace.setUUId()
            startPlace.setAddress(address)
            startPlace.setCoordinates(placeCoordinates)
            places.add(startPlace)


        }
        return places
    }

    /** [getStartPlaces]
     *  get the potential start [Place]s [List] that match the given text while typing in the
     *  input field in [FragmentMain]
     */
    override suspend fun getStartPlaces(query: String): List<Place> {

        return withContext(Dispatchers.IO) {
            try {

                val startPlaces = photonRetrofit.getAutocomplete(query, "5")

                processPhotonResponse(startPlaces.features)

            } catch (e: Exception) {

                Log.e("autocomplete", "Failed to fetch autocomplete", e)
                ArrayList()
            }
        }
    }

    /** [getReverseGeoCode]
     * get the [Place] that matches the location of the user's device
     * when using location for searching start place
     */
    override suspend fun getReverseGeoCode(coordinates: Coordinates): List<Place> {

        return withContext(Dispatchers.IO) {
            try {
                val startPlaces = photonRetrofit.getReverseGeoCode(
                    coordinates.getLatitude().toString(),
                    coordinates.getLongitude().toString()
                )

                processPhotonResponse(startPlaces.features)

            } catch (e: Exception) {

                Log.e("ReverseGeoCode", "Failed to fetch reverse geocode", e)
                ArrayList()
            }
        }
    }
}