package com.example.gtk_maps

import okhttp3.Interceptor
import okhttp3.OkHttpClient
/*import okhttp3.OkHttpClient.Builder.addInterceptor
import okhttp3.OkHttpClient.Builder.build
import okhttp3.Request.Builder.build*/
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class ClassRequests{
    private val photonApi: PhotonApi
    private val overpassApi: OverpassApi

    interface PhotonApi {
        @GET("api/")
        suspend fun getAutocomplete(
            @Query("q") query: String?,
            @Query("limit") limit: String?
        ): PhotonResponse

        @GET("reverse")
        suspend fun getReverseGeoCode(
            @Query("lat") latitude: String?,
            @Query("lon") longitude: String?
        ): PhotonResponse
    }
    interface OverpassApi {

        @GET("/api/interpreter")
        suspend fun getNearbyPlaces(
            @Query("data") query: String
        ): OverpassResponse
    }


    interface NearbyVolleyCallback {
        fun onResponse(results: ArrayList<ClassPlace>?)
        fun onFailure(error: String?)
    }

    interface GetAutocompleteCallback {
        fun onResponse(response: Response<PhotonResponse?>?)
        fun onFailure(t: Throwable?)
    }

    interface GetReverseGeoCodeCallback {
        fun onResponse(response: Response<PhotonResponse?>?)
        fun onFailure(t: Throwable?)
    }

    interface RouteCallback {
        fun onRouteReceived(result: String?)
        fun onRouteFailure()
    }

    var client: OkHttpClient = OkHttpClient().newBuilder()
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

    private var photonRetrofit = Retrofit.Builder()
        .baseUrl("https://photon.komoot.io/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val overpassRetrofit = Retrofit.Builder()
        .baseUrl("https://overpass-api.de")  // Az Overpass API alap URL-je
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    init {
        photonApi = photonRetrofit.create(PhotonApi::class.java)
        overpassApi = overpassRetrofit.create(OverpassApi::class.java)
    }

    /*fun findNearbyPlacesRequest(query: String,category: String, callback: NearbyVolleyCallback) {
        overpassApi.getNearbyPlaces(query).enqueue(object : Callback<OverpassResponse?> {

            override fun onResponse(p0: Call<OverpassResponse?>, response: Response<OverpassResponse?>) {
                if (response.isSuccessful && response.body() != null) {
                    val places = ArrayList<Place>()
                    try {
                        val elements = response.body()!!.elements
                        for (element in elements) {
                            val place = Place()
                            val address = Address()

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

                            place.setName( when {
                                tags.containsKey("name:hu") -> tags["name:hu"]!!
                                tags.containsKey("name:en") -> tags["name:en"]!!
                                tags.containsKey("name") -> tags["name"]!!
                                else -> "Ismeretlen"
                            })

                            if (tags.containsKey("cuisine")) place.setCuisine(tags["cuisine"]!!)
                            if (tags.containsKey("opening_hours")) place.setOpeningHours(tags["opening_hours"]!!)
                            if (tags.containsKey("charge")) place.setCharge(tags["charge"]!!)
                            if (tags.containsKey("addr:city")) address.setCity(tags["addr:city"]!!)
                            if (tags.containsKey("addr:street")) address.setStreet(tags["addr:street"]!!)
                            if (tags.containsKey("addr:housenumber")) address.setHouseNumber(tags["addr:housenumber"]!!)

                            place.setAddress(address)
                            places.add(place)
                        }
                        callback.onResponse(places)
                    } catch (e: Exception) {
                        callback.onFailure(e.localizedMessage)
                    }
                } else {
                    callback.onFailure("Sikertelen válasz az Overpass API-tól")
                }
            }

            override fun onFailure(p0: Call<OverpassResponse?>, t: Throwable) {
                callback.onFailure(t.localizedMessage)
            }
        })
    }

    //----------------------------------------------------------------------------------------------------------------
    //END OF findNearbyPlacesRequest
    //----------------------------------------------------------------------------------------------------------------
    fun getAutocompleteRequest(s: String?, getAutocompleteCallback: GetAutocompleteCallback) {
        photonApi.getAutocomplete(s, "5").enqueue(object : Callback<PhotonResponse?> {
            override fun onResponse(
                call: Call<PhotonResponse?>,
                response: Response<PhotonResponse?>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    getAutocompleteCallback.onResponse(response)
                }
            }

            override fun onFailure(call: Call<PhotonResponse?>, t: Throwable) {
                // Handle the error
                getAutocompleteCallback.onFailure(t)
            }
        })
    }

    fun getReverseCodeRequest(
        latitude: Double,
        longitude: Double,
        getReverseGeoCodeCallback: GetReverseGeoCodeCallback
    ) {
        photonApi.getReverseGeoCode(latitude.toString(), longitude.toString())
            .enqueue(object : Callback<PhotonResponse?> {
                override fun onResponse(
                    p0: Call<PhotonResponse?>,
                    response: Response<PhotonResponse?>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        getReverseGeoCodeCallback.onResponse(response)
                    }
                }

                override fun onFailure(p0: Call<PhotonResponse?>, t: Throwable) {
                    getReverseGeoCodeCallback.onFailure(t)
                }

            })
    }*/
}

    /*companion object {
        private const val API_KEY = "5b3ce3597851110001cf624822732185f95b41bbadd3ad38afd95ef0"
        private const val API_URL = "https://api.openrouteservice.org/v2/directions/"
        private val usefulTags = arrayOf("name", "cuisine", "opening_hours", "charge")
        private val toReturn = arrayOf("placeNames", "cuisines", "openingHours", "charges")
        fun getRoute(
            startLat: Double,
            startLon: Double,
            endLat: Double,
            endLon: Double,
            mode: String,
            routeCallback: RouteCallback?
        ) {
            object : AsyncTask<Void?, Void?, String?>() {
                protected override fun doInBackground(vararg voids: Void): String? {
                    var result: String? = null
                    try {
                        // A hálózati kérés elvégzése
                        val url = URL(
                            API_URL + mode + "?api_key=" + API_KEY +
                                    "&start=" + startLon + "," + startLat +
                                    "&end=" + endLon + "," + endLat
                        )
                        Log.d("openUrl", url.toString())
                        val urlConnection = url.openConnection() as HttpURLConnection
                        try {
                            val `in` = urlConnection.inputStream
                            val reader = BufferedReader(InputStreamReader(`in`))
                            val resultStringBuilder = StringBuilder()
                            var line: String?
                            while (reader.readLine().also { line = it } != null) {
                                resultStringBuilder.append(line).append("\n")
                            }
                            result = resultStringBuilder.toString()
                        } finally {
                            urlConnection.disconnect()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    return result
                }

                override fun onPostExecute(result: String?) {
                    super.onPostExecute(result)

                    // Ellenőrizd, hogy a result nem null és van-e hossza
                    if (result != null && result.length > 0) {
                        // További feldolgozás
                        routeCallback?.onRouteReceived(result)
                            ?: // Hibakezelés: RouteCallback null esetén
                            Log.e("OpenRouteServiceAPI", "RouteCallback is null")
                    } else {
                        // Hibakezelés, pl. üres vagy null válasz esetén
                        routeCallback?.onRouteFailure()
                            ?: // Hibakezelés: RouteCallback null esetén
                            Log.e("OpenRouteServiceAPI", "RouteCallback is null")
                    }
                }
            }.execute()
        }
    }*/
